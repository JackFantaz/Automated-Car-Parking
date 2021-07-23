package carparking.bls

import java.awt.Color
import javax.swing.JFrame

public class LedMock(title: String = "Led", x: Int = 30, y: Int = 250) : JFrame(title) {

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(300, 200)
        setLocation(x, y)
        turnOff()
        isVisible = true
    }

    fun turnOff() {
        contentPane.background = Color.BLACK
    }

    fun turnOn() {
        contentPane.background = Color.YELLOW
    }

}
