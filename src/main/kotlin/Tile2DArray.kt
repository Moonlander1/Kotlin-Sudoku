import javafx.scene.paint.Color

class Tile2DArray(size :Int) {
/* VÁLTOZÓK */
    private val tableSize = size
    private val table = Array(tableSize) { arrayOfNulls<Tile>(tableSize) }

    init {
        for(x in 0..8) for(y in 0..8) table[x][y] = Tile(0, x, y, Color.BLUE)
    }
    fun copy(copyThis : Tile2DArray) {
        for(x in 0..8) for(y in 0..8) table[x][y] = Tile(copyThis[x, y]!!.num, x, y, Color.BLUE)
    }

    operator fun get(row: Int, col: Int): Tile? {
        return table[row][col]
    }

}