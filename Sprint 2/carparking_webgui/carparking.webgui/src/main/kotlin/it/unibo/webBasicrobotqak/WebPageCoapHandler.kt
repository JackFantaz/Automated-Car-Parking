package it.unibo.webBasicrobotqak

import carparking.webgui.HIController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse


class WebPageCoapHandler(val controller: HIController, var channel: Channel<String>? = null) : CoapHandler {

    override fun onLoad(response: CoapResponse) {
        val content: String = response.responseText
        if (channel != null) runBlocking { channel!!.send(content) }
    }

    override fun onError() {
        System.err.println("WebPageCoapHandler  |  FAILED  ")
    }

}
