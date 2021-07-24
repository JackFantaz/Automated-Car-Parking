package carparking

import it.unibo.kactor.ActorBasic
import java.util.Observer
import it.unibo.kactor.ApplMessage
import java.util.Observable
import kotlinx.coroutines.runBlocking
import carparking.swing.ButtonMock

class ThermometerActor(name: String) : ActorBasic(name), Observer {

	private val button = ButtonMock("Thermometer", "New temperature", 350, 30);

	private var temperature = 30;

	init {
		button.addObserver(this)
	}

	override suspend fun actorBody(msg: ApplMessage) {}

	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	override fun update(o: Observable?, arg: Any?) {
		runBlocking {
			temperature = if (temperature > 35) 30 else 40
			emit("temperature", "temperature($temperature)")
		}
	}

}
