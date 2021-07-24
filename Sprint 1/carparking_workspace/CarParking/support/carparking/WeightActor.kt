package carparking

import it.unibo.kactor.ActorBasic
import java.util.Observer
import it.unibo.kactor.ApplMessage
import java.util.Observable
import kotlinx.coroutines.runBlocking
import carparking.swing.ButtonMock

class WeightActor(name: String) : ActorBasic(name), Observer {

	private val button = ButtonMock("Weight", "Toggle weight", 30, 30);

	private var present = false;

	init {
		button.addObserver(this)
	}

	override suspend fun actorBody(msg: ApplMessage) {}

	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	override fun update(o: Observable?, arg: Any?) {
		runBlocking {
			if (present) {
				present = false;
				emit("indoorCleared", "indoorCleared(0)")
			} else {
				present = true;
				emit("indoorOccupied", "indoorOccupied(0)")
			}
		}
	}

}
