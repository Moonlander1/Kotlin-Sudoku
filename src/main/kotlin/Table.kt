import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import kotlin.math.pow
import kotlin.random.Random

class Table : Thread() {
    /* VÁLTOZÓK */
    private val tableSize   = 9
    private var playingGrid = Tile2DArray(tableSize)
    private var solvedGrid  = Tile2DArray(tableSize)
    var solveSpeed = 0.0
    var solving = false
    var finished = false
    var mistakes = 0


    fun changeSolveSpeed(doubleSpeed: Boolean) {
        solveSpeed *= if(doubleSpeed) 2.0.pow(-1) else 2.0
        if(solveSpeed < 2.0)
            solveSpeed = 1.0
        if(solveSpeed > 128)
            solveSpeed = 256.0
    }
    fun getPlayingGrid(): Tile2DArray = playingGrid

    /* FUNCTIONS */
    init {
        generateTable()
        solvedGrid.copy(playingGrid)
        solveTable(solvedGrid, 0, 0)
    }

    @Override
    override fun run() {
        for(row in 0..8) for(col in 0..8)
            if(playingGrid[row,col]?.getColor() == Color.RED)
                playingGrid[row,col]?.num = 0
        solveSpeed = 16.0
        solving = true
        solveTable(playingGrid,0,0,true)
        solving = false
        checkIfFinished()
    }

    private fun solveTable(table: Tile2DArray, Row: Int, Column: Int, show: Boolean = false): Boolean {
        var row = Row
        var col = Column
        if (recursionEnd(row, col))
            return true
        if (endOfRow(col)) {
            row++
            col = 0
        }
        if (cellNotZero(table, row, col))
            return solveTable(table, row, (col + 1),show)
        for (num in 1..9) {
            if (canBePlaced(table, row, col, num)) {
                table[row, col]?.setNum(num,if(show) Color.GREEN else Color.BLACK)
                if (solveTable(table, row, (col + 1),show)) {
                    return true
                }
            }
            if(show)
                sleep(solveSpeed.toLong())
            table[row, col]?.setNum(0)
        }
        return false
    }

    fun putNumber(code: KeyCode) {
        val num: Int = code.code - 48
        for(row in 0..8) for(col in 0..8) {
            if(tileIsSelected(row,col)!!) {
                if(placedCorrectly(row,col,num) || num == 0) {
                    playingGrid[row, col]?.setNum(num, Color.BLUE)
                    checkIfNumberIsDone(num)
                    checkIfFinished()
                    if(differentSolution())
                        updateSolvedGrid()
                }
                else {
                    playingGrid[row, col]?.setNum(num, Color.RED)
                    mistakes++
                }
                playingGrid[row, col]?.selected = false
            }
        }
    }
        private fun updateSolvedGrid() {
        solvedGrid.copy(playingGrid)
        solveTable(solvedGrid, 0, 0)
    }
    fun hint() {
        var emptyCell = false
        for(col in 0..8) for(row in 0..8) if(playingGrid[row,col]?.num == 0)  emptyCell = true
        if(emptyCell) {
            val r = Random
            var row = r.nextInt(9)
            var col = r.nextInt(9)
            while(playingGrid[row,col]?.num != 0) {
                col = r.nextInt(9)
                row = r.nextInt(9)
            }
            playingGrid[row,col]?.setNum(solvedGrid[row,col]?.num!!,Color.GREEN)
            checkIfFinished()
        }
    }

    private fun generateTable() {
        val temp = Tile2DArray(tableSize)
        place8RandomNumbers(temp)
        solveTable(temp, 0, 0)
        show35NumbersFromFilled(temp)
    }

    private fun place8RandomNumbers(table: Tile2DArray) {
        val randomizer = Random
        var amountOfNumbers = 8
        for (i in 1..amountOfNumbers) {
            val row = randomizer.nextInt(9)
            val col = randomizer.nextInt(9)
            val num = randomizer.nextInt(9) + 1
            if (canBePlaced(table, row, col, num)) {
                table[row, col]?.setNum(num)
            } else
                amountOfNumbers++
        }
    }

    private fun show35NumbersFromFilled(table: Tile2DArray) {
        val randomizer = Random
        var shownNumbers = 35
        for (i in 1..shownNumbers) {
            val row = randomizer.nextInt(9)
            val col = randomizer.nextInt(9)
            if (!cellNotZero(playingGrid,row,col))
                playingGrid[row, col]?.setNum(table[row, col]?.num!!)
            else
                shownNumbers++
        }
    }

    private fun checkIfFinished() {
        for(row in 0..8) for( col in 0..8) {
            if(differentNumberInSolvedAndPlaying(row,col))
                return
        }
        finished = true
    }
    private fun checkIfNumberIsDone(num: Int) {
        var numberCounter = 0
        for(row in 0..8) for(col in 0..8)
            if(playingGrid[row,col]?.num == num)
                numberCounter++
        if(numberCounter == 9)
            for(row in 0..8) for(col in 0..8)
                if(playingGrid[row,col]?.num == num)
                    playingGrid[row,col]?.setNum(num,Color.YELLOW)
    }

    /* CONDITIONS */
    private fun cellNotZero(table: Tile2DArray, row: Int, col: Int): Boolean = (table[row, col]?.num != 0)
    private fun recursionEnd(row: Int, col: Int): Boolean = (row == tableSize - 1 && col == tableSize)
    private fun tileIsSelected(row: Int, col: Int): Boolean? = playingGrid[row,col]?.selected
    private fun canBePlaced(table: Tile2DArray, row: Int, col: Int, num: Int): Boolean {
        for (i in 0..8) if (table[row, i]?.num == num) {
            return false
        }
        for (i in 0..8) if (table[i, col]?.num == num) {
            return false
        }
        val sRow = row - row % 3
        val sCol = col - col % 3
        for (i in 0..2) for (j in 0..2) if (table[i + sRow, j + sCol]?.num == num) {
            return false
        }
        return true
    }
    private fun differentNumberInSolvedAndPlaying(row: Int, col: Int): Boolean {
        return playingGrid[row, col]?.num != solvedGrid[row, col]?.num
    }
    private fun placedCorrectly(row: Int, col: Int, num: Int): Boolean {
        val temp = Tile2DArray(tableSize)
        temp.copy(playingGrid)
        if(canBePlaced(temp,row,col,num))
            temp[row,col]?.setNum(num)
        else
            return false
        return solveTable(temp,0,0)
    }
    private fun endOfRow(col: Int): Boolean = (col == tableSize)
    private fun differentSolution():Boolean {
        for(row in 0..8) for(col in 0..8) {
            if(differentNumberInSolvedAndPlaying(row,col) && cellNotZero(playingGrid,row,col))
                return true
        }
        return false
    }

}

