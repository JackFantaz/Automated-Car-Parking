package carparking.webgui

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

    val clientSo = CarparkingSenderObserver("parkserviceguiactor", blocking = true)
    val fanSo = CarparkingSenderObserver("fanactor", blocking = false)
    val thermometerSo = CarparkingSenderObserver("thermometeractor", blocking = false)
    val serviceSo = CarparkingSenderObserver("parkmanagerserviceactor", blocking = false)
    val managerSo = CarparkingSenderObserver("parkservicestatusguiactor", blocking = false)
    val tempSentinelSo = CarparkingSenderObserver("temperaturesentinelactor", blocking = false)
    val outSentinelSo = CarparkingSenderObserver("outdoorsentinelactor", blocking = false)
    val trolleySo = CarparkingSenderObserver("trolleyactor", blocking = false)

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
        if (button == "exit_request" && token == "") {
            println("/carparking viewmodel=$viewmodel button=$button token=$token")
            viewmodel.addAttribute("received", "Please enter your TOKENID")
        } else {
            println("/carparking viewmodel=$viewmodel button=$button token=$token ...")
            when (button) {
                "enter_request" -> clientSo.forward("client_gui", "enterRequest", "enterRequest(0)")
                "car_enter" -> clientSo.forward("client_gui", "carEnter", "carEnter(0)")
                "exit_request" -> clientSo.forward("client_gui", "exitRequest", "exitRequest(${token.lowercase()})")
            }
            val answer = clientSo.observe()
            var received = ""
            if (parseType(answer) == "slotnum") received = "The SLOTNUM is ${parseArg(answer)}"
            else if (parseType(answer) == "tokenid") received = "The TOKENID is ${parseArg(answer)}"
            else if (parseType(answer) == "notice") received = parseArg(answer)
            else received = answer
            viewmodel.addAttribute("received", received)
            println("... answer=$answer received=$received")
        }
        return "clientGui"
    }

    @GetMapping("/status")
    fun homePageStatus(model: Model): String {
        println("/status $model")
        return "managerGui"
    }

    @PostMapping("/fan")
    fun fan(
        viewmodel: Model,
        @RequestParam(name = "dispatch", required = false, defaultValue = "") button: String
    ): String {
        if (button == "auto_fan") {
            println("/fan viewmodel=$viewmodel button=$button ...")
            val resource = managerSo.observe()
            val control = if (resource == "auto") "manual" else "auto"
            println("... resource=$resource control=$control")
            managerSo.forward("manager_gui", "fanAuto", "fanAuto($control)")
        } else {
            println("/fan viewmodel=$viewmodel button=$button")
            when (button) {
                "start_fan" -> managerSo.forward("manager_gui", "fanStart", "fanStart(0)")
                "stop_fan" -> managerSo.forward("manager_gui", "fanStop", "fanStop(0)")
            }
        }
        return "managerGui"
    }

    @PostMapping("/trolley")
    fun trolley(
        viewmodel: Model,
        @RequestParam(name = "dispatch", required = false, defaultValue = "") button: String
    ): String {
        println("/trolley viewmodel=$viewmodel button=$button")
        when (button) {
            "start_trolley" -> managerSo.forward("manager_gui", "startTrolley", "startTrolley(0)")
            "stop_trolley" -> managerSo.forward("manager_gui", "stopTrolley", "stopTrolley(0)")
        }
        return "managerGui"
    }

    @GetMapping("/ajax")
    @ResponseBody
    fun ajax(@RequestParam(name = "about", required = false, defaultValue = "") about: String): String {
        // println("/ajax about=$about ...")
        val answer = when (about) {
            "temp" -> parseArg(thermometerSo.observe()) + " °C"
            "slots" -> serviceSo.observe()
            "fan" -> (if (parseType(fanSo.observe()) == "fanStart") "ON" else if (parseType(fanSo.observe()) == "fanStop") "OFF" else "").plus(
                " ("
            ).plus(
                managerSo.observe().plus(")")
            )
            "tempAlarm" -> if (parseType(tempSentinelSo.observe()) == "temperatureAlarm") "TEMPERATURE ALARM!<br>" else ""
            "outAlarm" -> if (parseType(outSentinelSo.observe()) == "outdoorAlarm") "OUTDOOR ALARM!<br>" else ""
            "trolley" -> trolleySo.observe()
            else -> ""
        }
        // println("... answer=$answer")
        return answer
    }

    private fun parseArg(message: String): String {
        return message.substringAfter("(").reversed().substring(1).reversed()
    }

    private fun parseType(message: String): String {
        return message.split("(")[0]
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
