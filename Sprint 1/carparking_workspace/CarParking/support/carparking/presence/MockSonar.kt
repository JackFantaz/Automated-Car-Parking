package carparking.presence

class MockSonar : Sonar {

    private val mock = SliderMock("Sonar Distance", x = 30, y = 30)

    override fun getDistance(): Int {
        return mock.getValue()
    }

}
