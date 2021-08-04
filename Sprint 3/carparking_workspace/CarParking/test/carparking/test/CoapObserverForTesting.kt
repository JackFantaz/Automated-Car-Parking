package carparking.test

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import kotlinx.coroutines.channels.Channel

class CoapObserverForTesting(
	val observed: String,
	val context: String = "ctxcarparking",
	val port: String = "60000",
	val name: String = "testingobs"
) {

	private var client: CoapClient? = null
	private lateinit var handler: CoapHandler
	private val uriStr = "coap://localhost:$port/$context/$observed"

	fun setup(channel: Channel<String>, expected: String? = null) {
		client = CoapClient()
		client!!.uri = uriStr
		handler = UpdateCoapHandler("h_$name", channel, expected)
	}

	fun addObserver(channel: Channel<String>, expected: String? = null) {
		setup(channel, expected)
		client!!.observe(handler)
	}

	fun terminate() { }

}
