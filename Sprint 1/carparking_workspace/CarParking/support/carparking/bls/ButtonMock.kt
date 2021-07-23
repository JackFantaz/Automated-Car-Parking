package carparking.bls

import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.Observable
import javax.swing.JButton
import javax.swing.JFrame

class ButtonMock(title: String = "Button", label: String = "Click me!", x: Int = 30, y: Int = 30) : Observable(), ActionListener {

    private val frame = JFrame(title)
    private val button = JButton(label)

    init {
        button.font = Font(button.font.name, button.font.style, 26)
        button.addActionListener(this)
        frame.contentPane.add(button)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(300, 200)
        frame.setLocation(x, y)
        frame.isVisible = true
    }

    override fun actionPerformed(e: ActionEvent?) {
        setChanged()
        notifyObservers()
    }

}
