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
class MultiSlotTest {

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
	fun checkSlots() {
		runBlocking {
			
			var cco = CarparkingCoapObserver("parkserviceguiactor", blocking = true, verbose = true)
			
			//SIX CARS + 1
			println("checkSlots -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			var slotnum6 = cco.observePayload()
			
			println("checkSlots -> forward carEnter(0)")
			actor!!.forward("carEnter", "carEnter(0)", "parkmanagerserviceactor")
			var tokenid6 = cco.observePayload()
			assertLocationInTime("6", "0", "N", 10000) //indoor
			assertLocationInTime("4", "3", "W", 10000) //parking6
		
			println("checkSlots -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			var slotnum5 = cco.observePayload()

			println("checkSlots -> forward carEnter(0)")
			actor!!.forward("carEnter", "carEnter(0)", "parkmanagerserviceactor")
			var tokenid5 = cco.observePayload()
			assertLocationInTime("6", "0", "N", 10000) //indoor
			assertLocationInTime("4", "2", "W", 10000) //parking5

			println("checkSlots -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			var slotnum4 = cco.observePayload()
			
			println("checkSlots -> forward carEnter(0)")
			actor!!.forward("carEnter", "carEnter(0)", "parkmanagerserviceactor")
			var tokenid4 = cco.observePayload()
			assertLocationInTime("6", "0", "N", 10000) //indoor
			assertLocationInTime("4", "1", "W", 10000) //parking4
			
			println("checkSlots -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			var slotnum3 = cco.observePayload()
			
			println("checkSlots -> forward carEnter(0)")
			actor!!.forward("carEnter", "carEnter(0)", "parkmanagerserviceactor")
			var tokenid3 = cco.observePayload()
			assertLocationInTime("6", "0", "N", 10000) //indoor
			assertLocationInTime("1", "3", "E", 10000) //parking3
			
			println("checkSlots -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			var slotnum2 = cco.observePayload()
			
			println("checkSlots -> forward carEnter(0)")
			actor!!.forward("carEnter", "carEnter(0)", "parkmanagerserviceactor")
			var tokenid2 = cco.observePayload()
			assertLocationInTime("6", "0", "N", 10000) //indoor
			assertLocationInTime("1", "2", "E", 10000) //parking2
			
			println("checkSlots -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			var slotnum1 = cco.observePayload()
			
			println("checkSlots -> forward carEnter(0)")
			actor!!.forward("carEnter", "carEnter(0)", "parkmanagerserviceactor")
			var tokenid1 = cco.observePayload()
			assertLocationInTime("6", "0", "N", 10000) //indoor
			assertLocationInTime("1", "1", "E", 10000) //parking1
			assertLocationInTime("0", "0", "S", 10000) //home
			
			println("checkSlots -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			var slotnum0 = cco.observePayload()
			if ( slotnum0.toInt()==0 ) { println("Car parking full!") }
			
			//EXIT 4

			println("checkSlots -> forward exitRequest($tokenid4)")
			actor!!.forward("exitRequest", "exitRequest($tokenid4)", "parkmanagerserviceactor")
			assertNotice(cco, "exitRequest(received)")
			assertLocationInTime("4", "1", "W", 10000) //parking4
			assertLocationInTime("6", "4", "S", 10000) //oudoor
			
			//EXIT 2
			
			println("checkSlots -> forward exitRequest($tokenid2)")
			actor!!.forward("exitRequest", "exitRequest($tokenid2)", "parkmanagerserviceactor")
			assertNotice(cco, "exitRequest(received)")
			assertLocationInTime("1", "2", "E", 10000) //parking2
			assertLocationInTime("6", "4", "S", 10000) //oudoor
			assertLocationInTime("0", "0", "S", 120000) //home
	
			//WRONG TOKENID
			
			println("checkSlots -> forward exitRequest($tokenid4)")
			actor!!.forward("exitRequest", "exitRequest($tokenid4)", "parkmanagerserviceactor")
			assertNotice(cco, "tokenid(invalid)")
			

			//ENTER PARKING4
			
			println("checkSlots -> forward enterRequest(0)")
			actor!!.forward("enterRequest", "enterRequest(0)", "parkmanagerserviceactor")
			var slotnum42 = cco.observePayload()
			
			println("checkSlots -> forward carEnter(0)")
			actor!!.forward("carEnter", "carEnter(0)", "parkmanagerserviceactor")
			var tokenid42 = cco.observePayload()
			assertLocationInTime("6", "0", "N", 10000) //indoor
			assertLocationInTime("4", "1", "W", 10000) //parking4
			
			//EXIT 6
			
			println("checkSlots -> forward exitRequest($tokenid6)")
			actor!!.forward("exitRequest", "exitRequest($tokenid6)", "parkmanagerserviceactor")
			assertNotice(cco, "exitRequest(received)")
			
			//EXIT 1
			
			println("checkSlots -> forward exitRequest($tokenid1)")
			actor!!.forward("exitRequest", "exitRequest($tokenid1)", "parkmanagerserviceactor")
			assertNotice(cco, "exitRequest(received)")
			
			//EXIT 3
			
			println("checkSlots -> forward exitRequest($tokenid3)")
			actor!!.forward("exitRequest", "exitRequest($tokenid3)", "parkmanagerserviceactor")
			assertNotice(cco, "exitRequest(received)")
			
			//EXIT 4
			
			println("checkSlots -> forward exitRequest($tokenid42)")
			actor!!.forward("exitRequest", "exitRequest($tokenid42)", "parkmanagerserviceactor")
			assertNotice(cco, "exitRequest(received)")
			
			//EXIT 5
			
			println("checkSlots -> forward exitRequest($tokenid5)")
			actor!!.forward("exitRequest", "exitRequest($tokenid5)", "parkmanagerserviceactor")
			assertNotice(cco, "exitRequest(received)")
			assertLocationInTime("0", "0", "S", 120000) //home

		}
	}
/*
	private suspend fun consume() {
		while (obsChannel.poll() != null) {
			println("~~~ CONSUMED DIRTY DATA ON CHANNEL")
			print("~~~ > ")
			readLine()
		}
	}

	private suspend fun observe(actor: String, messages: Array<String>) {
		observer = CoapObserverForTesting(actor)
		for (m in messages) observer!!.addObserver(obsChannel, m)
	}
*/
	
	private suspend fun assertNotice(observer: CarparkingCoapObserver, notice: String, verbose: Boolean = true) {
		var result = observer.observePayload()
		if (verbose) {
			if (result == notice) println("assertNotice ->  correct notice $result detected")
			else println("assertNotice -> wrong notice $result detected instead of $notice")
		}
		assertEquals(result, notice)		
	}
	
	private suspend fun assertEvent(observer: CarparkingCoapObserver, event: String, verbose: Boolean = true) {
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
