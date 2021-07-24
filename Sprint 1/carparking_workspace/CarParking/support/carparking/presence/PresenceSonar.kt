package carparking.presence

class PresenceSonar(val sonar: Sonar, val threshold: Int = 40) : PresenceSensor {

	override fun isPresent(): Boolean {
		return sonar.getDistance() <= threshold
	}

}
