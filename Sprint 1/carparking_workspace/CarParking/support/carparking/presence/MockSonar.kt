package carparking.presence

import carparking.swing.SliderMock

class MockSonar : Sonar {

	private val mock = SliderMock("Sonar", x = 50, y = 240)

	override fun getDistance(): Int {
		return mock.getValue()
	}

}
