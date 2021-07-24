package carparking.etc

import carparking.presence.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import carparking.temperature.MockThermometer
import carparking.temperature.MockFan
import carparking.temperature.Thermometer
import carparking.temperature.Fan

fun main() {
	val sonar = PresenceSonar(MockSonar())
	val weight = PresenceWeight(MockWeightSensor())
	val thermometer = MockThermometer()
	val fan = MockFan()
	runBlocking {
		while (true) {
			println()
			printPresence("OutSonar       ", sonar)
			printPresence("WeightSensor   ", weight)
			printTemperature("RoomThermometer", thermometer)
			operateFan("CoolingFan     ", fan)
			delay(1000)
		}
	}
}

fun printPresence(name: String, sensor: PresenceSensor) {
	println("$name -> ${sensor.isPresent()}")
}

fun printTemperature(name: String, thermometer: Thermometer) {
	println("$name -> ${thermometer.getTemperature()}")
}

fun operateFan(name: String, fan: Fan) {
	if (fan.isOn()) fan.turnOff()
	else fan.turnOn()
	println("$name -> ${fan.isOn()}")
}
