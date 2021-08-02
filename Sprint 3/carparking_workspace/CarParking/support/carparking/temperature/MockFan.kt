package carparking.temperature

import carparking.swing.LedMock
import java.awt.Color

class MockFan : Fan {

	private val mock = LedMock("Fan", x = 370, y = 240)

	override fun turnOn() {
		mock.turnOn()
	}

	override fun turnOff() {
		mock.turnOff()
	}

	override fun isOn(): Boolean {
		return mock.isOn()
	}

}
