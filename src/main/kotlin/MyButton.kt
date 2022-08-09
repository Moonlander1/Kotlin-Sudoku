import com.sun.javafx.tk.Toolkit
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font


class MyButton(
    private val x: Double, private val y: Double,
    private val w: Double, private val h: Double, private val text: String = ""
) {
    /* VÁLTOZÓK */
    lateinit var fillColor: Color
    lateinit var hoverColor: Color
    lateinit var clickedColor: Color
    lateinit var textColor: Color
    val actionCommand = text
    var hovered = false
    var clicked = false

    /* FÜGGVÉNYEK */
    fun pointInsideButton(a: Double, b: Double): Boolean = (x <= a && a < x + w && y <= b && b < y + h)
    fun draw(batch: GraphicsContext) {
        batch.fill = Color.BLACK
        batch.fillRect(x - 2, y - 2, w + 4, h + 4)

        batch.fill = if (clicked) clickedColor else if (hovered) hoverColor else fillColor
        batch.fillRect(x, y, w, h)

        val metrics = Toolkit.getToolkit().fontLoader.getFontMetrics(batch.font)
        var stringWidth = 0.0f
        for (i in text) stringWidth += metrics.getCharWidth(i)

        batch.font = Font(30.0)
        batch.fill = textColor
        batch.fillText(text, x + (w - stringWidth) / 2, y + 23.0 + (h - 23.0) / 2)
    }

}