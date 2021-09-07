package carparking.presence

import java.io.BufferedReader
import java.io.InputStreamReader

class SonarDriver : Sonar {

	override fun getDistance(): Int {
		return machineRequest("./sonar").toInt()
	}

	private fun machineRequest(command: String, verbose: Boolean = false): String {
		if (verbose) println(command)
		val process = Runtime.getRuntime().exec(command)
		val reader = BufferedReader(InputStreamReader(process.inputStream))
		val line = reader.readText();
		if (verbose) println(line)
		val exitVal = process.waitFor()
		if (verbose) println(exitVal)
		reader.close()
		return line
	}

}
