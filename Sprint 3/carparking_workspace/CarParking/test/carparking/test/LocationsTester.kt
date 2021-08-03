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
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class LocationsTester {

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
				delay(3000)
				syncChannel.send("starttesting")
			}
		}

		@JvmStatic
		@AfterClass
		fun afterAll() { runBlocking { delay(3000) } }

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
	fun afterEach() { runBlocking { delay(3000) } }

	@Test
	fun checkLocations() {
		runBlocking {
			
			actor!!.forward("goto", "goto(indoor)", "trolleyactor")
			delay(10000)
			actor!!.forward("goto", "goto(outdoor)", "trolleyactor")
			delay(10000)
			actor!!.forward("goto", "goto(parking1)", "trolleyactor")
			delay(5000)
			actor!!.forward("goto", "goto(parking2)", "trolleyactor")
			delay(5000)
			actor!!.forward("goto", "goto(parking3)", "trolleyactor")
			delay(5000)
			actor!!.forward("goto", "goto(parking4)", "trolleyactor")
			delay(5000)
			actor!!.forward("goto", "goto(parking5)", "trolleyactor")
			delay(5000)
			actor!!.forward("goto", "goto(parking6)", "trolleyactor")
			delay(10000)
			actor!!.forward("goto", "goto(home)", "trolleyactor")
		}
	}

	private suspend fun consume() {
		while (obsChannel.poll() != null) {
			println("~~~ CONSUMED DIRTY DATA ON CHANNEL")
			print("~~~ > ")
			readLine()
		}
	}

	private suspend fun observe(actor: String, messages: Array<String>) {
		// if (observer != null ) (observer as CoapObserverForTesting).terminate()
		// while (obsChannel.poll() != null) delay(100)
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

	private suspend fun assertNoEventInTime(millis: Int, verbose: Boolean = true) {
		var counter = 0;
		var result: String? = null;
		do {
			result = obsChannel.poll()
			delay(100)
			counter++
		} while (result == null && counter < millis / 100)
		if (verbose) {
			if (counter >= millis / 100) println("assertNoEventInTime -> no events detected")
			else println("assertNoEventInTime -> event $result detected within ${counter * 100} ms")
		}
		assert(counter >= millis / 100)
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
