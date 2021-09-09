package carparking.temperature

import carparking.swing.SliderMock

class MockThermometer : Thermometer {

	private val mock = SliderMock("Thermometer", start = 20, x = 370, y = 50)

	override fun getTemperature(): Float {
		return mock.getValue().toFloat()
	}

}
