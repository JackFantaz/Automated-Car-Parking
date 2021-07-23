import carparking.presence.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    val sonar = PresenceSonar(MockSonar())
    val weight = PresenceWeight(MockWeightSensor())
    runBlocking {
        while (true) {
            printPresence("OutSonar   ", sonar)
            printPresence("WeightSensor", weight)
            delay(500)
        }
    }
}

fun printPresence(name: String, sensor: PresenceSensor) {
    println("$name\t-->\t${sensor.isPresent()}")
}