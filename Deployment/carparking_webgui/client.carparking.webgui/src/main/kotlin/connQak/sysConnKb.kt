package connQak

val mqtthostAddr    = "localhost"	//broker.hivemq.com
val mqttport		= "1883"
val mqtttopic       = "unibo/basicrobot"
// var robothostAddr   = "192.168.1.191"
var robothostAddr   = "172.16.0.2"
val robotPort     	= "60000"
val qakdestination 	= "parkmanagerserviceactor"
val ctxqakdest      = "ctxcarparking"
val connprotocol    = ConnectionType.TCP //TCP COAP HTTP MQTT

//fun main(){ println("consoles") }