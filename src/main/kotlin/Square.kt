import javafx.scene.paint.Color
import kotlin.random.Random

class Square(var x: Double, var y: Double) {
    /* VÁLTOZÓK */
    var w: Double = 50.0
    var h: Double = 50.0
    var fillColor: Color = Color.WHITE

    private val r = Random
    val picX = r.nextInt(9)
    val picY = r.nextInt(4) + 1

    fun insideSquare(a: Double, b: Double): Boolean = x < a && a < x + w && y < b && b < y + h
}