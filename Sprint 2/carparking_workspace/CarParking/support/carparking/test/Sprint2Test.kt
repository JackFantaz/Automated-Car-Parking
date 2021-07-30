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
import org.junit.After
import org.junit.Assert.assertEquals

class Sprint2Test {

	companion object {

		var actor: ActorBasic? = null
		var syncChannel = Channel<String>()
		var obsChannel = Channel<String>()

		var observer: CoapObserverForTesting? = null
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
				syncChannel.send("starttesting")
			}
		}

		@JvmStatic
		@AfterClass
		fun afterAll() {
			//runBlocking { delay(10000) }
		}

	}

	@Before
	fun beforeEach() {
		if (!started) {
			runBlocking {
				syncChannel.receive()
				started = true
			}
		}
	}

	@After
	fun afterEach() {
		//observer!!.terminate()
		//observer = null
		runBlocking { delay(1000) }
	}
	
	@Test
	fun checkDoors() {
		runBlocking {
		
			println("checkDoors -> emit indoorOccupied(0)")
			actor!!.emit("indoorOccupied", "indoorOccupied(0)")
			
			println("checkDoors -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			
			println("checkDoors -> forward carEnter(1)")
			actor!!.forward("carEnter", "carEnter(1)", "parkmanagerserviceactor")
			
			assertNotMovingInTime(3000)
			
			println("checkDoors -> emit indoorCleared(0)")
			actor!!.emit("indoorCleared", "indoorCleared(0)")
			
			println("checkDoors -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			
			println("checkDoors -> forward carEnter(1)")
			actor!!.forward("carEnter", "carEnter(1)", "parkmanagerserviceactor")
			
			assertLocationInTime("6", "0", "N", 10000)
			assertLocationInTime("0", "0", "S", 20000)
			
			println("checkDoors -> emit outdoorOccupied(0)")
			actor!!.emit("outdoorOccupied", "outdoorOccupied(0)")
			
			println("checkDoors -> forward exitRequest(0)")
			actor!!.forward("exitRequest", "exitRequest(0)", "parkmanagerserviceactor")
			
			assertNotMovingInTime(3000)
			
			println("checkDoors -> emit outdoorCleared(0)")
			actor!!.emit("outdoorCleared", "outdoorCleared(0)")
			
			println("checkDoors -> forward exitRequest(0)")
			actor!!.forward("exitRequest", "exitRequest(0)", "parkmanagerserviceactor")
			
			assertLocationInTime("6", "4", "S", 20000)
			assertLocationInTime("0", "0", "S", 50000)
			
		}
	}

	@Test
	fun checkSensorsAndActuators() {
		runBlocking {

			observe("weightactor", arrayOf("indoorOccupied", "indoorCleared"))
			println("checkSensorsAndActuators -> please raise weight above threshold (default 60)")
			assertEvent("indoorOccupied(0)")
			println("checkSensorsAndActuators -> please lower weight below threshold (default 60)")
			assertEvent("indoorCleared(0)")

			observe("sonaractor", arrayOf("outdoorOccupied", "outdoorCleared"))
			println("checkSensorsAndActuators -> please bring sonar below threshold (default 40)")
			assertEvent("outdoorOccupied(0)")
			println("checkSensorsAndActuators -> please move sonar above threshold (default 40)")
			assertEvent("outdoorCleared(0)")

			println("checkSensorsAndActuators -> please set temperature to 80.0 degrees and press ENTER on console")
			print("> ")
			readLine()
			observe("thermometeractor", arrayOf("temperature"))
			assertEvent("temperature(80.0)")

			actor!!.forward("fanStart", "fanStart(0)", "fanactor")
			println("checkSensorsAndActuators -> please wait for fan to turn on and press ENTER on console")
			print("> ")
			readLine()
			actor!!.forward("fanStop", "fanStop(0)", "fanactor")
			println("checkSensorsAndActuators -> please wait for fan to turn off and press ENTER on console")
			print("> ")
			readLine()

		}
	}

	private fun observe(actor: String, messages: Array<String>) {
		observer = CoapObserverForTesting(actor)
		for (m in messages) observer!!.addObserver(obsChannel, m)
	}

	private suspend fun assertEvent(event: String, verbose: Boolean = true) {
		var result = obsChannel.receive()
		if (verbose) {
			if (result == event) println("assertEvent -> correct event $result detected")
			else println("assertEvent -> wrong event $result detected instead of $event")
		}
		assertEquals(result, event)
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
