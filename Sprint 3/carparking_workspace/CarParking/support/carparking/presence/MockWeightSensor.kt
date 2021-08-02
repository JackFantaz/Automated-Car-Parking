package carparking.presence

import carparking.swing.SliderMock

class MockWeightSensor : WeightSensor {

	private val mock = SliderMock("Weight", x = 50, y = 50)

	override fun getWeight(): Int {
		return mock.getValue()
	}

}
