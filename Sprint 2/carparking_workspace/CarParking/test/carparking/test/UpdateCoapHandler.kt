package carparking.test

import kotlinx.coroutines.channels.Channel
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.CoAP
import kotlinx.coroutines.runBlocking

class UpdateCoapHandler(
	val name: String,
	val channel: Channel<String>,
	val expected: String? = null
): CoapHandler {

	override fun onLoad(response: CoapResponse) {
		val content = response.responseText
		if (response.code == CoAP.ResponseCode.NOT_FOUND) return
		if (expected != null && content.contains(expected)) runBlocking { channel.send(content) }
	}

	override fun onError() { println("$name | FAILED") }

}
