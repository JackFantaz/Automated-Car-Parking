package carparking.presence

class MockWeightSensor : WeightSensor {

    private val mock = SliderMock("Weight Sensor", x = 30, y = 250)

    override fun getWeight(): Int {
        return mock.getValue()
    }

}
