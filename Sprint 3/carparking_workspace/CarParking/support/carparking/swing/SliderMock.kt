package carparking.swing

import java.awt.Font
import java.util.*
import javax.swing.JFrame
import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class SliderMock(title: String = "Slider", min: Int = 0, max: Int = 100, start: Int = 50, /*snap: Boolean = false,*/ x: Int = 30, y: Int = 30) : Observable(),
	ChangeListener {

	private val frame = JFrame(title)
	private val slider = JSlider(min, max)

	init {
		slider.font = Font(slider.font.name, slider.font.style, 14)
		slider.majorTickSpacing = 10
		slider.paintTicks = true
		slider.paintLabels = true
		slider.value = start
		// slider.snapToTicks = snap
		slider.addChangeListener(this)
		frame.contentPane.add(slider)
		frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
		frame.setSize(320, 180)
		frame.setLocation(x, y)
		frame.isVisible = true
	}

	fun getValue(): Int {
		return slider.value
	}

	override fun stateChanged(e: ChangeEvent?) {
		setChanged()
		notifyObservers()
	}

}
