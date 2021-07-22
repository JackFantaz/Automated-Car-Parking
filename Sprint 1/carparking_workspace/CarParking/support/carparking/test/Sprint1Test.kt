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
import itunibo.planner.plannerUtil
import carparking.directionalPlanner

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
					actor = QakContext.getActor("parkserviceguiactor")
				}
				delay(2000)
				channel.send("starttesting")
			}
		}

		@JvmStatic
		@AfterClass
		fun afterAll() {
			//runBlocking { delay(60000) }
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
	fun checkPath() {
		runBlocking {

			assertNotMovingInTime(5000)
			println("checkPath -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			assertLocationInTime("6", "0", "N", 20000)
			assertLocationInTime("1", "1", "E", 10000)
			assertLocationInTime("0", "0", "S", 10000)

			println("checkPath -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			assertNotMovingInTime(5000)

			println("checkPath -> forward exitRequest(1)")
			actor!!.forward("exitRequest", "exitRequest(1)", "parkmanagerserviceactor")
			assertLocationInTime("1", "1", "E", 10000)
			assertLocationInTime("6", "4", "S", 10000)
			assertLocationInTime("0", "0", "S", 20000)
			assertNotMovingInTime(5000)

		}
	}

	private suspend fun assertLocationInTime(x: String, y: String, d: String, millis: Int, verbose: Boolean = true) {
		var counter = 0;
		while (!(x == directionalPlanner.getX() && y == directionalPlanner.getY() && d == directionalPlanner.getD()) && counter < millis / 100) {
			delay(100)
			counter++
		}
		if (verbose) {
			if (counter < millis / 100) println("assertLocationInTime -> target [$x,$y,$d] reached in ${counter * 100} ms")
			else println("assertLocationInTime -> target [$x,$y,$d] not reached in time")
		}
		assert(counter < millis / 100)
	}

	private suspend fun assertNotMovingInTime(millis: Int, verbose: Boolean = true) {
		var counter = 0;
		val posX = directionalPlanner.getX()
		val posY = directionalPlanner.getY()
		val posD = directionalPlanner.getD()
		while ((directionalPlanner.getX() == posX && directionalPlanner.getY() == posY && directionalPlanner.getD() == posD) && counter < millis / 100) {
			delay(100)
			counter++
		}
		if (verbose) {
			if (counter >= millis / 100) println("assertNotMovingInTime -> no movement detected")
			else println("assertNotMovingInTime -> movement detected within ${counter * 100} ms")
		}
		assert(counter >= millis / 100)
	}

}
