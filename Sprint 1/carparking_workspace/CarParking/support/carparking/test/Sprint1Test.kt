package carparking.test

import org.junit.BeforeClass
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import it.unibo.kactor.QakContext
import it.unibo.kactor.ActorBasic
import kotlinx.coroutines.channels.Channel
import org.junit.Before
import org.junit.Test
import org.junit.AfterClass
import kotlin.jvm.JvmStatic

class Sprint1Test {

	companion object {

		var actor: ActorBasic? = null
		var channel = Channel<String>()
		var started = false

		@JvmStatic
		@BeforeClass
		fun beforeAll() {
			GlobalScope.launch { it.unibo.ctxcarparking.main() }
			GlobalScope.launch {
				while (actor == null) {
					println("waiting for system startup...")
					delay(500)
					actor = QakContext.getActor("trolleyactor")
				}
				delay(2000)
				channel.send("starttesting")
			}
		}

		@JvmStatic
		@AfterClass
		fun afterAll() {
			runBlocking { delay(60000) }
		}

	}

	@Before
	fun beforeEach() {
		if (!started) {
			runBlocking {
				channel.receive()
				started = true
			}
		}
	}

	@Test
	fun test1() {
		runBlocking {
			println("Sprint1Test -> send enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			delay(15000)
			println("Sprint1Test -> send enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			delay(5000)
			println("Sprint1Test -> send exitRequest(1)")
			actor!!.forward("exitRequest", "exitRequest(1)", "parkmanagerserviceactor")
		}
	}

}
