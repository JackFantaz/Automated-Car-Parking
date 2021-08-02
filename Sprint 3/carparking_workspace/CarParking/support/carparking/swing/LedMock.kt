package carparking.swing

import java.awt.Color
import javax.swing.JFrame

public class LedMock(title: String = "Led", private val color: Color = Color.YELLOW, x: Int = 30, y: Int = 30) :
	JFrame(title) {

	init {
		defaultCloseOperation = JFrame.EXIT_ON_CLOSE
		setSize(320, 180)
		setLocation(x, y)
		turnOff()
		isVisible = true
	}

	fun turnOff() {
		contentPane.background = Color.BLACK
	}

	fun turnOn() {
		contentPane.background = color
	}

	fun isOn(): Boolean {
		return contentPane.background != Color.BLACK
	}

}
