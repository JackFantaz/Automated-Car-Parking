package carparking.presence

class PresenceWeight(val weightSensor: WeightSensor, val threshold: Int = 60) : PresenceSensor {

    override fun isPresent(): Boolean {
        return weightSensor.getWeight() >= threshold
    }

}
