package carparking.test

import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.CoapClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

class CarparkingCoapObserver(val actor: String, val blocking: Boolean = true, val verbose: Boolean = false) :
	CoapHandler {

	private val client = CoapClient("coap://localhost:60000/ctxcarparking/$actor")
	private val channel = Channel<String>()

	init {
		if (blocking) client.observe(this)
	}

	override fun onLoad(response: CoapResponse): Unit {
		var result = response.responseText ?: ""
		runBlocking { channel.send(result) }
	}

	override fun onError(): Unit {}

	suspend fun observe(): String {
		var result: String
		if (blocking) result = channel.receive()
		else result = client.get().getResponseText() ?: ""
		if (verbose) println("observe($actor) ~> $result")
		return result
	}

	suspend fun observePayload(): String {
		var result: String
		if (blocking) result = channel.receive()
		else result = client.get().getResponseText() ?: ""
		result = result.split("(")[1].reversed().substring(1).reversed()
		if (verbose) println("observePayload($actor) ~> $result")
		return result
	}

}
