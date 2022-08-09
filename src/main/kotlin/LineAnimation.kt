import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color

class LineAnimation(private val batch: GraphicsContext, private val x: Double, private val y: Double) {
    private var squares  = mutableListOf<Square>()
    private val numberOfSquares = 15

    companion object {
        lateinit var numbers: Image
        var mouseX  = 0.0
        var mouseY  = 0.0
        var pressed = false
    }


    init {
        for(i in 0 until numberOfSquares)
            squares.add(Square(x, y + i * 90.0))
    }

    /* Moves and draws all the squares */
    fun drawAnimations() {
        moveSquares()
        mouseMovementHandling()
        mousePressedHandling()
        drawLines()
        drawSquares()
        drawNumbers()
    }

    private fun mouseMovementHandling() {
        for(s in squares) {
            if (s.insideSquare(mouseX,mouseY))
                s.fillColor = Color.rgb(230,230,200,1.0)
            else
                s.fillColor = Color.WHITE
        }
    }
    private fun mousePressedHandling() {
        if(pressed) {
            for (s in squares) {
                if (s.insideSquare(mouseX, mouseX)) {
                    val num = s.picX + 1
                    println("The number is: $num")
                }
            }
            pressed = false
        }
    }

    private fun moveSquares() {
        for(s in squares) {
            s.y += 1.3
            if(s.y > numberOfSquares * 90.0 - 50.0)
                s.y = -50.0
        }
    }
    private fun drawLines() {
        batch.fill = Color.BLACK
        batch.fillRect(x + 25.0,0.0,2.0,650.0)
    }
    private fun drawSquares() {
        for(s in squares) {
            batch.fill = Color.BLACK
            batch.fillRect(s.x - 1,s.y - 1, s.w + 2, s.h + 2)
            batch.fill = s.fillColor
            batch.fillRect(s)
        }
    }
    private fun drawNumbers() {
        for(s in squares)
            batch.drawImage(
                numbers,s.picX.toDouble() * 60.0,s.picY.toDouble() * 60.0,60.0,60.0,
            s.x,s.y,s.w,s.h)
    }
}
private fun GraphicsContext.fillRect(square: Square) {
    this.fillRect(square.x,square.y,square.w,square.h)
}

