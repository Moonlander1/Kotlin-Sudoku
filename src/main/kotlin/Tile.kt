import javafx.scene.paint.Color

class Tile(var num: Int, row: Int, col: Int, private var colour: Color) {
    //Top left corner
    val x = getScreenPosition(row, 90.0)
    val y = getScreenPosition(col, 150.0)
    var selected = false
    fun getColor(): Color = colour
    fun setNum(Number: Int, c: Color = Color.BLACK) {
        num = Number
        colour = c
    }

/* VÁLTOZÓK */

/* FÜGGVÉNYEK */
private fun getScreenPosition(num: Int, base: Double): Double {
    val insideBorder = 4.0
    val insideGridBorder = 2.0
    return base + num * 52.0 + when(num) {
        in 0..2 -> (insideBorder - insideGridBorder) * 0.0
        in 3..5 -> (insideBorder - insideGridBorder) * 1.0
        else    -> (insideBorder - insideGridBorder) * 2.0
    }
}
}