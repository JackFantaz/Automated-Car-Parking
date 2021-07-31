package carparking.webgui

import connQak.ConnectionType
import connQak.connQakBase
import it.unibo.kactor.ApplMessage
import it.unibo.kactor.MsgUtil
import it.unibo.webBasicrobotqak.CoapSupport
import it.unibo.webBasicrobotqak.WebPageCoapHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class HIController {

    @Value("\${spring.application.name}")
    var appName: String? = null

    val carparkingAddress = "127.0.0.1"
    val carparkingContext = "ctxcarparking"

    var fanStatus = ""
    var tempStatus = ""
    var slotStatus = ""

    val clientTopic = "parkserviceguiactor"
    val clientObserver = WebPageCoapHandler(this, null)
    var clientChannel = Channel<String>()
    lateinit var clientConnection: connQakBase
    lateinit var clientSupport: CoapSupport

    val fanTopic = "fanactor"
    val fanObserver = WebPageCoapHandler(this, null)
    var fanChannel = Channel<String>()
    lateinit var fanConnection: connQakBase
    lateinit var fanSupport: CoapSupport

    val thermometerTopic = "thermometeractor"
    val thermometerObserver = WebPageCoapHandler(this, null)
    var thermometerChannel = Channel<String>()
    lateinit var thermometerConnection: connQakBase
    lateinit var thermometerSupport: CoapSupport

    val serviceTopic = "parkmanagerserviceactor"
    val serviceObserver = WebPageCoapHandler(this, null)
    var serviceChannel = Channel<String>()
    lateinit var serviceConnection: connQakBase
    lateinit var serviceSupport: CoapSupport

    init {

        connQak.robothostAddr = carparkingAddress

        clientConnection = connQakBase.create(ConnectionType.TCP)
        clientConnection.createConnection()
        clientSupport =
            CoapSupport("coap://${connQak.robothostAddr}:${connQak.robotPort}", "$carparkingContext/$clientTopic")
        clientSupport.observeResource(clientObserver)

        fanConnection = connQakBase.create(ConnectionType.TCP)
        fanConnection.createConnection()
        fanSupport = CoapSupport("coap://${connQak.robothostAddr}:${connQak.robotPort}", "$carparkingContext/$fanTopic")
        fanSupport.observeResource(fanObserver)

        thermometerConnection = connQakBase.create(ConnectionType.TCP)
        thermometerConnection.createConnection()
        thermometerSupport =
            CoapSupport("coap://${connQak.robothostAddr}:${connQak.robotPort}", "$carparkingContext/$thermometerTopic")
        thermometerSupport.observeResource(thermometerObserver)

        serviceConnection = connQakBase.create(ConnectionType.TCP)
        serviceConnection.createConnection()
        serviceSupport =
            CoapSupport("coap://${connQak.robothostAddr}:${connQak.robotPort}", "$carparkingContext/$serviceTopic")
        serviceSupport.observeResource(serviceObserver)

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
            "car_enter" -> MsgUtil.buildDispatch(
                "clientsgui",
                "carEnter",
                "carEnter(0)",
                connQak.qakdestination
            )
            "exit_request" -> MsgUtil.buildDispatch(
                "clientsgui",
                "exitRequest",
                "exitRequest(${token.lowercase()})",
                connQak.qakdestination
            )
            else -> null
        }
        val answer = sendDispatchCheckCoap(message, clientObserver, clientChannel, clientConnection)
        /*if (answer.contains("valTemp")) answer = "${parseArg(answer)}"
        else if (answer.contains("fanStatus")) answer = "${parseArg(answer)}"
        else if (answer.contains("trolleyStatus")) answer = "${parseArg(answer)}"
        else answer = ""*/
        var received = ""
        if (parseType(answer) == "slotnum") received = "The SLOTNUM is ${parseArg(answer)}"
        else if (parseType(answer) == "tokenid") received = "The TOKENID is ${parseArg(answer)}"
        else if (parseType(answer) == "notice") received = answer.substring(7).reversed().substring(1).reversed()
        else received = answer
        viewmodel.addAttribute("received", received)
        println("... answer=$answer receivedFan=$received")
        return "clientGui"
    }


    @GetMapping("/status")
    fun homePageStatus(model: Model): String {
        println("/status $model")
        model.addAttribute("receivedTemp", "")
        model.addAttribute("receivedFan", "")
        model.addAttribute("receivedTrolley", "")
        model.addAttribute("receivedSlot", "")
        return "managerGui"
    }

    /*@PostMapping("/status_carparking")
    fun carparking2(
        viewmodel: Model,
        @RequestParam(name = "dispatch", required = false, defaultValue = "") button: String
    ): String {
        println("/carparking viewmodel=$viewmodel button=$button ...")

        val message = when (button) {
            "start_fan" -> MsgUtil.buildDispatch(
                "managergui",
                "fanStart",
                "fanStart(0)",
                fanTopic
            )
            "stop_fan" -> MsgUtil.buildDispatch(
                "managergui",
                "fanStop",
                "fanStop(0)",
                fanTopic
            )
            /*   "auto_fan" -> MsgUtil.buildDispatch(
                   "managergui",
                   "autoFan",
                   "autoFan(0)",
                   connQak.qakdestination
               )
               "start_trolley" -> MsgUtil.buildDispatch(
                   "managergui",
                   "startTrolley",
                   "startTrolley(0)",
                   connQak.qakdestination
               )
               "stop_trolley" -> MsgUtil.buildDispatch(
                   "managergui",
                   "stopTrolley",
                   "stopTrolley(0)",
                   connQak.qakdestination
               )

             */
            else -> null
        }

        if (message != null) {

            var answer = ""
            fanObserver.channel = fanChannel
            fanConnection.forward(message)
            runBlocking {
                answer = fanChannel.receive()
                fanObserver.channel = null
            }
            println("... answer=$answer")

            if (answer.contains("temperature")) answer = "${parseArg(answer)}   [TMAX=35°]"
            else answer = ""
            viewmodel.addAttribute("receivedTemp", answer)

            if (answer.contains("fan")) answer = "${parseArg(answer)}"
            else answer = ""
            viewmodel.addAttribute("receivedFan", answer)

            if (answer.contains("trolley")) answer = "${parseArg(answer)}"
            else answer = ""
            viewmodel.addAttribute("receivedTrolley", answer)
        } else {
            viewmodel.addAttribute("received2", "")
        }

        return "managerGui"
    }*/

    @PostMapping("/fan")
    fun fan(
        viewmodel: Model,
        @RequestParam(name = "dispatch", required = false, defaultValue = "") button: String
    ): String {
        println("/fan viewmodel=$viewmodel button=$button ...")
        val message = when (button) {
            "start_fan" -> MsgUtil.buildDispatch(
                "managergui",
                "fanStart",
                "fanStart(0)",
                fanTopic
            )
            "stop_fan" -> MsgUtil.buildDispatch(
                "managergui",
                "fanStop",
                "fanStop(0)",
                fanTopic
            )
            else -> null
        }
        val answer = sendDispatchCheckCoap(message, fanObserver, fanChannel, fanConnection)
        val received = if (parseType(answer) == "fanStart") "ON" else if (parseType(answer) == "fanStop") "OFF" else ""
        // viewmodel.addAttribute("receivedFan", received)
        fanStatus = received
        addStatusAttributes(viewmodel)
        println("... answer=$answer receivedFan=$received")
        return "managerGui"
    }

    /*@PostMapping("/temperature")
    fun temperature(
        viewmodel: Model,
    ): String {
        println("/temperature viewmodel=$viewmodel ...")
        var answer = thermometerSupport.readResource()
        val received = parseArg(answer)
        // viewmodel.addAttribute("receivedTemp", received)
        tempStatus = received
        addStatusAttributes(viewmodel)
        println("... answer=$answer receivedTemp=$received")
        return "managerGui"
    }*/

    /*@PostMapping("/slots")
    fun slots(
        viewmodel: Model,
    ): String {
        println("/slots viewmodel=$viewmodel ...")
        var answer = serviceSupport.readResource()
        val received = parseArg(answer)
        // viewmodel.addAttribute("receivedSlot", received)
        slotStatus = received
        addStatusAttributes(viewmodel)
        println("... answer=$answer receivedSlot=$received")
        return "managerGui"
    }*/

    @GetMapping("/ajax")
    @ResponseBody
    fun ajax(@RequestParam(name = "about", required = false, defaultValue = "") about: String): String {
        // println("/ajax about=$about ...")
        val answer = when (about) {
            "temp" -> parseArg(thermometerSupport.readResource())
            "slots" -> parseArg(serviceSupport.readResource())
            "fan" -> if (parseType(fanSupport.readResource()) == "fanStart") "ON" else if (parseType(fanSupport.readResource()) == "fanStop") "OFF" else ""
            else -> ""
        }
        // println("... answer=$answer")
        return answer
    }

    private fun sendDispatchCheckCoap(
        dispatch: ApplMessage?,
        observer: WebPageCoapHandler,
        channel: Channel<String>,
        connection: connQakBase
    ): String {
        var answer = ""
        if (dispatch != null) {
            observer.channel = channel
            connection.forward(dispatch)
            runBlocking {
                answer = channel.receive()
                observer.channel = null
            }
        }
        return answer
    }

    private fun parseArg(message: String): String {
        return message.split("(", ")")[1]
    }

    private fun parseType(message: String): String {
        return message.split("(", ")")[0]
    }

    private fun addStatusAttributes(model: Model) {
        model.addAttribute("receivedTemp", tempStatus)
        model.addAttribute("receivedFan", fanStatus)
        model.addAttribute("receivedSlot", slotStatus)
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
