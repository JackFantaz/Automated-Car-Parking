package carparking.webgui

import connQak.ConnectionType
import connQak.connQakBase
import connQak.robotPort
import connQak.robothostAddr
import it.unibo.kactor.MsgUtil
import kotlinx.coroutines.GlobalScope
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.CoapClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CarparkingSenderObserver(
    private val actor: String,
    private val blocking: Boolean = true,
    private val verbose: Boolean = false
) : CoapHandler {

    private var client: CoapClient? = null
    private var channel: Channel<String>? = null
    private var connection: connQakBase? = null

    private fun initObserver() {
        client = CoapClient("coap://$robothostAddr:$robotPort/ctxcarparking/$actor")
        client!!.timeout = 750
        if (blocking) {
            channel = Channel<String>()
            client!!.observe(this)
            observe()
        }
        if (verbose) println("CarparkingSenderObserver($actor, b=$blocking).initObserver")
    }

    private fun initSender() {
        connection = connQakBase.create(ConnectionType.TCP)
        connection!!.createConnection()
        if (verbose) println("CarparkingSenderObserver($actor, b=$blocking).initSender")
    }

    override fun onLoad(response: CoapResponse) {
        GlobalScope.launch {
            val result = response.responseText ?: ""
            if (verbose) println("CarparkingSenderObserver($actor, b=$blocking).onLoad ~> $result")
            channel!!.send(result)
        }
    }

    override fun onError() {
        if (verbose) println("CarparkingSenderObserver($actor, b=$blocking).onError")
    }

    fun observe(): String {
        if (client == null) initObserver()
        var result = ""
        if (blocking) {
            runBlocking {
                result = channel!!.receive()
                while (!channel!!.isEmpty) {
                    if (verbose) println("CarparkingSenderObserver($actor, b=$blocking).observe ~> $result (discarded)")
                    result = channel!!.receive()
                }
            }
        } else {
            val response = client!!.get()
            result = if (response != null) response.responseText ?: "" else ""
        }
        if (verbose) println("CarparkingSenderObserver($actor, b=$blocking).observe ~> $result")
        return result
    }

    fun forward(name: String, id: String, content: String) {
        if (connection == null) initSender()
        val dispatch = MsgUtil.buildDispatch(name, id, content, actor)
        if (verbose) println("CarparkingSenderObserver($actor, b=$blocking).forward ~> $content")
        connection!!.forward(dispatch)
    }

}
