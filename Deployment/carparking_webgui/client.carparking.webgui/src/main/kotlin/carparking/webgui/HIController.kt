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

    private fun parseArg(message: String): String {
        return if (message.length >= 2) message.substringAfter("(").reversed().substring(1).reversed() else ""
    }

    private fun parseType(message: String): String {
        return message.substringBefore("(")
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
