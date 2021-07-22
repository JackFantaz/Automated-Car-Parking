package carparking.test

import it.unibo.actor0.ActorBasicKotlin
import it.unibo.actor0.ApplMessage
import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.MsgUtil
import it.unibo.robotService.ApplMsgs
import it.unibo.robotService.BasicStepRobotActor
import kotlinx.coroutines.delay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import org.junit.BeforeClass
import org.junit.Assert.*
import java.net.UnknownHostException
import cli.System.IO.IOException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import it.unibo.kactor.QakContext
import org.junit.Before
import it.unibo.kactor.ActorBasic
import org.junit.AfterClass
import it.unibo.kactor.sysUtil
import org.junit.After
import it.unibo.kactor.QakContextServer
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import kotlinx.coroutines.runBlocking
import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.CoapObserveRelation

class Tester {

	//val planner = carparking.DirectionalPlanner("parkingMap")
	val home = arrayOf("0", "0", "S")
	val parking = arrayOf("1", "1", "E")
	val indoor = arrayOf("6", "0", "N")
	val outdoor = arrayOf("6", "4", "S")

	companion object {

		var myactor: ActorBasic? = null
		//var planner = carparking.DirectionalPlanner("parkingMap")
		var channelSyncStart = Channel<String>()
		var testingObserver: CoapObserverForTesting? = CoapObserverForTesting()
		var channelForObserver = Channel<String>()
		var systemStarted = false

		@JvmStatic
		@BeforeClass
		fun init() {
			GlobalScope.launch { it.unibo.ctxcarparking.main() }
			GlobalScope.launch {
				while (myactor == null) {
					println("waiting for system startup...")
					delay(500)
					myactor = QakContext.getActor("trolleyactor")
				}
				delay(2000)
				channelSyncStart.send("starttesting")
			}
		}

	}

	@Before
	fun checkSystemStarted() {
		if (!systemStarted) {
			runBlocking {
				channelSyncStart.receive()
				systemStarted = true
			}
			testingObserver!!.addObserver(channelForObserver, "moveactivated")
			testingObserver!!.addObserver(channelForObserver, "stepDone")
		}
		//if( testingObserver == null) testingObserver = CoapObserverForTesting("obstesting${counter++}")
	}

	@After
	fun removeObs() {
		testingObserver!!.terminate()
		testingObserver = null
		runBlocking {
			delay(1000)
		}
	}

	@Test
	fun test1() {
		runBlocking {
			moveForward()
			assertStep("350")
			turnLeft()
			assertMove("l")
			moveForward()
			assertStep("350")
			turnRight()
			assertMove("r")
			moveForward()
			assertStep("350")
		}
	}

	suspend fun assertMove(move: String) {
		var result = channelForObserver.receive()
		println("MOVE result=$result expected=moveactivated($move)")
		assertEquals(result, "moveactivated($move)")
	}

	suspend fun assertStep(time: String) {
		var result = channelForObserver.receive()
		println("STEP result=$result expected=stepDone($time)")
		assertEquals(result, "stepDone($time)")
	}

	suspend fun readObserver(): String {
		return channelForObserver.receive()
	}

	suspend fun moveForward() {
		myactor!!.request("step", "step(350)", "basicrobot")
		delay(450)
	}

	suspend fun turnLeft() {
		myactor!!.forward("cmd", "cmd(l)", "basicrobot")
		delay(450)
	}

	suspend fun turnRight() {
		myactor!!.forward("cmd", "cmd(r)", "basicrobot")
		delay(450)
	}

}

fun externalRobotMove(move: String) {
	runBlocking {
		machineRequest("curl -d \"{\\\"robotmove\\\":\\\"$move\\\", \\\"time\\\": 350}\" -X POST 127.0.0.1:8090/api/move")
		delay(450)
	}
}

fun machineRequest(command: String, verbose: Boolean = false): String {
	if (verbose) println(command)
	val process = Runtime.getRuntime().exec(command)
	val reader = BufferedReader(InputStreamReader(process.inputStream))
	val line = reader.readText();
	if (verbose) println(line)
	val exitVal = process.waitFor()
	if (verbose) println(exitVal)
	reader.close()
	return line
}

class UpdateHandler(
	val name: String, val channel: Channel<String>,
	val expected: String? = null
) : CoapHandler {

	override fun onLoad(response: CoapResponse) {
		val content = response.responseText
		//println("	%%%%%% $name | content=$content  expected=$expected RESP-CODE=${response.code} ")
		if (response.code == CoAP.ResponseCode.NOT_FOUND) return
		if (content.contains("START") || content.contains("created")) return
		if (expected != null && content.contains(expected))
			runBlocking { channel.send(content) }
	}

	override fun onError() {
		println("$name | FAILED")
	}
}

class CoapObserverForTesting(
	val name: String = "testingobs",
	val context: String = "ctxbasicrobot",
	val observed: String = "basicrobot",
	val port: String = "8020"
) {
	private var client: CoapClient? = null
	private lateinit var handler: CoapHandler
	private val uriStr = "coap://localhost:$port/$context/$observed"

	fun setup(channel: Channel<String>, expected: String? = null) {
		client = CoapClient()
		//println("	%%%%%% $name | START uriStr: $uriStr - expected=$expected")
		client!!.uri = uriStr
		handler = UpdateHandler("h_$name", channel, expected)
	}

	fun addObserver(channel: Channel<String>, expected: String? = null) {
		setup(channel, expected)
		client!!.observe(handler)
	}

	fun terminate() {
		//println("	%%%%%% $name | terminate $handler")
	}
}
