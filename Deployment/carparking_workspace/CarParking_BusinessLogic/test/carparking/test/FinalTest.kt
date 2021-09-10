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
import org.eclipse.californium.core.CoapClient

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FinalTest {

	companion object {

		var actor: ActorBasic? = null
		var syncChannel = Channel<String>()
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
		fun afterAll() {
			runBlocking { delay(2000) }
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
		runBlocking { delay(2000) }
	}
	
	@Test
	fun statusCheck() {
		runBlocking {
		
			val parkserviceObs = CarparkingCoapObserver("parkserviceguiactor", blocking=false)
			
			actor!!.forward("enterRequest", "enterRequest(0)", "parkserviceguiactor")
			actor!!.forward("carEnter", "carEnter(0)", "parkserviceguiactor")
			assertLocationInTime("6", "0", "N", 60000)
			assertLocationInTime("4", "3", "W", 60000)
			val tokenid1 = parkserviceObs.observePayload()
			actor!!.forward("enterRequest", "enterRequest(0)", "parkserviceguiactor")
			actor!!.forward("carEnter", "carEnter(0)", "parkserviceguiactor")
			assertLocationInTime("6", "0", "N", 60000)
			assertLocationInTime("4", "2", "W", 60000)
			val tokenid2 = parkserviceObs.observePayload()
			assertLocationInTime("0", "0", "S", 60000)
			
			
			actor!!.emit("temperature", "temperature(20.0)")
			actor!!.forward("fanStop", "fanStop(0)", "fanactor")
			actor!!.emit("indoorCleared", "indoorCleared(0)")
			actor!!.emit("outdoorCleared", "outdoorCleared(0)")
			
			actor!!.forward("enterRequest", "enterRequest(0)", "parkserviceguiactor")
			delay(2000)
			actor!!.forward("carEnter", "carEnter(0)", "parkserviceguiactor")
			delay(2000)
			actor!!.forward("exitRequest", "exitRequest($tokenid1)", "parkserviceguiactor")
			assertLocationInTime("0", "0", "S", 60000)
			
			
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

	private suspend fun assertEvent(observer: CarparkingCoapObserver, event: String, verbose: Boolean = true) {
		if (!observer.isBlocking()) delay(500)
		var result = observer.observe()
		if (verbose) {
			if (result == event) println("assertEvent -> correct event $result detected")
			else println("assertEvent -> wrong event $result detected instead of $event")
		}
		assertEquals(result, event)
	}

	private suspend fun assertNoEventInTime(observer: CarparkingCoapObserver, millis: Int, verbose: Boolean = true) {
		var counter = 0;
		var result: String? = null;
		do {
			result = observer.pollNewValue()
			delay(100)
			counter++
		} while (result == null && counter < millis / 100)
		if (verbose) {
			if (counter >= millis / 100) println("assertNoEventInTime -> no events detected")
			else println("assertNoEventInTime -> event $result detected within ${counter * 100} ms")
		}
		assert(counter >= millis / 100)
	}

	private suspend fun assertSlotnum(observer: CarparkingCoapObserver, slotnum: String, verbose: Boolean = true) {
		if (!observer.isBlocking()) delay(500)
		var result = observer.observePayload()
		if (verbose) {
			if (result == slotnum) println("assertSlotnum -> correct SLOTNUM $result detected")
			else println("assertSlotnum -> wrong SLOTNUM $slotnum detected instead of $slotnum")
		}
		assertEquals(result, slotnum)
	}

	private suspend fun assertNotice(observer: CarparkingCoapObserver, notice: String, verbose: Boolean = true) {
		if (!observer.isBlocking()) delay(500)
		var result = observer.observePayload()
		if (verbose) {
			if (result == notice) println("assertNotice -> correct notice $result detected")
			else println("assertNotice -> wrong notice $result detected instead of $notice")
		}
		assertEquals(result, notice)
	}

}
