package carparking.webgui

import connQak.ConnectionType
import connQak.connQakBase
import it.unibo.kactor.MsgUtil
import it.unibo.webBasicrobotqak.CoapSupport
import it.unibo.webBasicrobotqak.WebPageCoapHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class HIController {

    @Value("\${spring.application.name}")
    var appName: String? = null

    val carparkingAddress = "127.0.0.1"
    val coapObserver = WebPageCoapHandler(this, null)
    var answerChannel = Channel<String>()
    lateinit var carparkingConnection: connQakBase
    lateinit var coapsupport: CoapSupport

    init {
        connQak.robothostAddr = carparkingAddress
        carparkingConnection = connQakBase.create(ConnectionType.TCP)
        carparkingConnection.createConnection()
        coapsupport =
            CoapSupport("coap://${connQak.robothostAddr}:${connQak.robotPort}", "ctxcarparking/parkserviceguiactor")
        coapsupport.observeResource(coapObserver)
    }

    @GetMapping("/")
    fun homePage(model: Model): String {
        println("/ $model")
        model.addAttribute("received", "")
        return "clientGui"
    }

    @PostMapping("/carparking")
    fun carparking(
        viewmodel: Model,
        @RequestParam(name = "dispatch", required = false, defaultValue = "") button: String,
        @RequestParam(name = "token", required = false, defaultValue = "") token: String
    ): String {
        println("/carparking viewmodel=$viewmodel button=$button token=$token ...")

        val message = when (button) {
            "enter_request" -> MsgUtil.buildDispatch(
                "clientsgui",
                "enterRequest",
                "enterRequest(0)",
                connQak.qakdestination
            )
            "car_enter" -> MsgUtil.buildDispatch("clientsgui", "carEnter", "carEnter(0)", connQak.qakdestination)
            "exit_request" -> MsgUtil.buildDispatch(
                "clientsgui",
                "exitRequest",
                "exitRequest(${token.lowercase()})",
                connQak.qakdestination
            )
            else -> null
        }

        if (message != null) {

            var answer = ""
            coapObserver.channel = answerChannel
            carparkingConnection.forward(message)
            runBlocking {
                answer = answerChannel.receive()
                coapObserver.channel = null
            }
            println("... answer=$answer")

            if (answer.contains("slotnum")) answer = "The SLOTNUM is ${parseArg(answer)}"
            else if (answer.contains("tokenid")) answer = "The TOKENID is ${parseArg(answer)}"
            else answer = ""
            viewmodel.addAttribute("received", answer)

        } else {
            viewmodel.addAttribute("received", "")
        }

        return "clientGui"
    }

    private fun parseArg(message: String): String {
        return message.split("(", ")")[1]
    }

    @ExceptionHandler
    fun handle(ex: Exception): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        return ResponseEntity(
            "BaseController ERROR ${ex.message}",
            responseHeaders, HttpStatus.CREATED
        )
    }

}
