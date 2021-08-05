package carparking.webgui

import connQak.ConnectionType
import connQak.connQakBase
import it.unibo.kactor.ApplMessage
import it.unibo.kactor.MsgUtil
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.CoapClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

class CarparkingSenderObserver(val actor: String, val blocking: Boolean = true, val verbose: Boolean = false) :
    CoapHandler {

    private val client = CoapClient("coap://localhost:60000/ctxcarparking/$actor")
    private val channel = Channel<String>()
    private val connection = connQakBase.create(ConnectionType.TCP)

    private lateinit var previous: String

    init {
        if (blocking) {
            client.observe(this)
            runBlocking { previous = channel.receive() }
        }
        connection.createConnection()
    }

    override fun onLoad(response: CoapResponse): Unit {
        val result = response.responseText ?: ""
        runBlocking { channel.send(result) }
    }

    override fun onError(): Unit {}

    fun observe(): String {
        var result = ""
        if (blocking) runBlocking { result = channel.receive() }
        else result = client.get().getResponseText() ?: ""
        previous = result
        if (verbose) println("observe($actor) ~> $result")
        return result
    }

    fun forward(name: String, id: String, content: String) {
        val dispatch = MsgUtil.buildDispatch(name, id, content, actor)
        connection.forward(dispatch)
    }

}
