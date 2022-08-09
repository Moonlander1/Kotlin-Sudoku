import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import java.awt.Rectangle
import java.util.*

class SudokuGUI(private val batch: GraphicsContext) {
/* VÁLTOZÓK */
    private var sudoku: Table = Table()
    private lateinit var numbers: Image
    private lateinit var selected: Image
    private lateinit var hover: Image
    private lateinit var grid: Image

    private val pastelYellow = Color.rgb(253,253,150,1.0)
    private val pastelGreen  = Color.rgb(190,231,176,1.0)
    private val pastelRed    = Color.rgb(255,105,97 ,1.0)
    private val darkBlue     = Color.rgb( 18,77 ,120,1.0)

    private val tileS        =  50.0
    private val baseX        =  90.0
    private val baseY        = 150.0
    private val gridS        = 154.0
    private val insideBorder =   4.0
    private var selectRow    =    -1
    private var selectCol    =    -1
    private val borderRect   = Rectangle(
        (baseX - insideBorder).toInt(),
        (baseY - insideBorder).toInt(),
        (3 * gridS + 4 * insideBorder).toInt(),
        (3 * gridS + 4 * insideBorder).toInt()
    )

    private val buttons = LinkedList<MyButton>()
    private val animations = LinkedList<LineAnimation>()

    /* FUNCTIONS */
    init {
        loadGraphics()
        loadingButtons()
        loadAnimations()
    }
    fun update() {
        drawEverything()
        gameOver()
    }

    /* Calls all the drawMethods */
    private fun drawEverything() {
        drawGrids()
        drawNumbers()
        drawHover()
        drawSelected()
        drawButtons()
        drawStrings()
        drawAnimations()
    }

    /* Input handling */
    fun mouseMovement(event: MouseEvent) {
        val x = event.sceneX
        val y = event.sceneY
        val row = whichTile(x,baseX)
        val col = whichTile(y,baseY)
        LineAnimation.mouseX = x
        LineAnimation.mouseY = y
        if(insidePlayingField(x,y) && notXColorTile(row,col,Color.BLACK) &&
            notXColorTile(row,col,Color.GREEN) && notXColorTile(row,col,Color.YELLOW)) {
            if(differentTile(row,col)) {
                nullSelected()
            }
            selectRow = row
            selectCol = col
        }
        else {
            nullSelected()
            selectRow = -1
            selectCol = -1
            hoveringButton(x,y)
        }
    }
    fun mousePressed(event: MouseEvent) {
        mouseMovement(event)
        LineAnimation.pressed = true
        if(selectRow != -1 && selectCol != -1) {
            sudoku.getPlayingGrid()[selectRow,selectCol]?.selected = true
        }
        clickingButton(event.sceneX,event.sceneY)
    }
    fun mouseReleased(event: MouseEvent) {
        releasingButton(event.sceneX,event.sceneY)
    }
    fun keyTyped(event: KeyEvent) {
        val keyPressed = event.code.toString()
        if(sudoku.solving) {
            if(keyPressed == "S")
                sudoku.changeSolveSpeed(false)
            else if(keyPressed == "F")
                sudoku.changeSolveSpeed(true)
        }
        else {
            when (keyPressed) {
                in "DIGIT0".."DIGIT9" -> {
                    sudoku.putNumber(event.code)
                }
                "S" -> if(!sudoku.finished) sudoku.start()
                "H" -> sudoku.hint()
            }
        }
    }

    /* Helper functions */
    private fun gameOver() {
        if(sudoku.finished) {
            batch.font = Font(100.0)
            batch.fill = Color.SANDYBROWN
            for(i in 0..4)
                batch.fillText("Good Job!",101.0 + i,410.0)
            batch.fill = Color.SADDLEBROWN
            batch.fillText("Good Job!",100.0,410.0)
            batch.font = Font(30.0)
        }
    }
    private fun loadAnimations() {
        animations.add(LineAnimation(batch, 17.0, -10.0))
        animations.add(LineAnimation(batch, 581.0, -60.0))
        LineAnimation.numbers = this.numbers
    }
    private fun loadGraphics() {
        numbers  = Image(getResource("/Numbers.png"))
        grid     = Image(getResource("/grid.png"))
        hover    = Image(getResource("/highlight.png"))
        selected = Image(getResource("/selected.png"))
    }

    /* Button managing */
    private fun loadingButtons() {
        buttons.add(MyButton(baseX - 2.0, 10.0, 200.0, 60.0, "New Game"))
        buttons.add(MyButton(baseX - 2.0, 80.0, 200.0, 60.0, "Hint"))
        for(b in buttons) {
            b.fillColor    = pastelYellow
            b.hoverColor   = Color.YELLOW
            b.clickedColor = pastelGreen
            b.textColor    = darkBlue
        }

    }
    private fun hoveringButton(x: Double, y: Double) {
        for(b in buttons)
            b.hovered = b.pointInsideButton(x,y)
    }
    private fun clickingButton(x: Double, y: Double) {
        for(b in buttons)
            b.clicked = b.pointInsideButton(x,y)
    }
    private fun releasingButton(x: Double, y: Double) {
        for(b in buttons) {
            if (b.pointInsideButton(x, y)) {
                when(b.actionCommand) {
                    "New Game" -> sudoku = Table()
                    "Hint"     -> sudoku.hint()
                }
            }
            b.clicked = false
            b.hovered = false
        }
    }

    /* Drawing functions */
    private fun drawGrids() {
        batch.fill = Color.BLACK
        batch.fillRect(borderRect)
        for(i in 0..8) {
            if(i <= 2)
                batch.drawImage(grid,baseX + i       * (gridS + insideBorder),
                    baseY,gridS,gridS)
            else if(i <= 5)
                batch.drawImage(grid,baseX + (i - 3) * (gridS + insideBorder),
                    baseY + gridS + insideBorder,gridS,gridS)
            else
                batch.drawImage(grid,baseX + (i - 6) * (gridS + insideBorder),
                    baseY + 2 * (gridS + insideBorder),gridS,gridS)
        }
    }
    private fun drawNumbers() {
        for(row in 0..8) for(col in 0..8) {
            if(tileNotZero(sudoku.getPlayingGrid(),row,col)) {
                val x = sudoku.getPlayingGrid()[row,col]?.x
                val y = sudoku.getPlayingGrid()[row,col]?.y
                val num = sudoku.getPlayingGrid()[row,col]?.num
                val sy = when(sudoku.getPlayingGrid()[row,col]?.getColor()){
                    Color.BLACK -> 0.0
                    Color.BLUE  -> 1.0
                    Color.RED   -> 2.0
                    Color.GREEN -> 3.0
                    else        -> 4.0
                }
                batch.drawImage(numbers, (num!! - 1) * 60.0,sy * 60.0,60.0,60.0,x!!,y!!,tileS,tileS)
            }
        }
    }
    private fun drawHover() {
        if(!tileIsSelected()!! && hoveringSomething()) {
            val drawX = sudoku.getPlayingGrid()[selectRow, selectCol]?.x
            val drawY = sudoku.getPlayingGrid()[selectRow, selectCol]?.y
            batch.drawImage(hover, drawX!!, drawY!!, 50.0, 50.0)
        }
    }
    private fun drawSelected() {
        if(tileIsSelected()!!) {
            val drawX = sudoku.getPlayingGrid()[selectRow,selectCol]?.x
            val drawY = sudoku.getPlayingGrid()[selectRow,selectCol]?.y
            batch.drawImage(selected, drawX!!, drawY!!, 50.0, 50.0)
        }
    }
    private fun drawButtons() {
        for(button in buttons)
            button.draw(batch)
    }
    private fun drawStrings() {
        batch.font = Font(30.0)
        batch.fill = Color.BLACK
        batch.fillRect(baseX + 208.0,8.0 ,266.0,64.0)
        batch.fillRect(baseX + 208.0,78.0,266.0,64.0)
        batch.fill = pastelRed
        batch.fillRect(baseX + 210.0,10.0,262.0,60.0)
        batch.fill = pastelGreen
        batch.fillRect(baseX + 210.0,80.0,262.0,60.0)
        batch.fill = darkBlue
        batch.fillText("Mistakes: ${sudoku.mistakes}",baseX + 213.0,51.5)
        batch.fillText("Solving speed: ${sudoku.solveSpeed/16}x",baseX + 213.0,121.5,260.0)
    }
    private fun drawAnimations() {
        for(a in animations)
            a.drawAnimations()
    }

    /* Input handling helper functions */
    private fun whichTile(spot: Double, base: Double): Int {
        return when(spot) {
            in base..base + gridS ->
                whichTileInside3x3(base,spot,0)
            in base + gridS + insideBorder..base + 2 * gridS + insideBorder ->
                whichTileInside3x3(base + gridS + insideBorder,spot,1)
            in base + 2 * gridS + insideBorder..base + 3 * gridS + 2 * insideBorder ->
                whichTileInside3x3(base + 2 * (gridS + insideBorder),spot,2)
            else -> -1
        }
    }
    private fun whichTileInside3x3(base: Double, spot: Double, whichTriplet: Int): Int {
        return when(spot) {
            in base..base + tileS -> 0 + 3 * whichTriplet
            in base + tileS+ 2.0..base + 2.0 + 2 * tileS -> 1 + 3 * whichTriplet
            in base + 2.0 + 2 * tileS..base + 4.0 + 3 * tileS-> 2 + 3 * whichTriplet
            else -> -1
        }
    }
    private fun nullSelected() {
        for(row in 0..8) for(col in 0..8) sudoku.getPlayingGrid()[row,col]?.selected = false
    }

    /* CONDITIONS for Mouse */
    private fun tileNotZero(table: Tile2DArray, row: Int, col: Int): Boolean = table[row,col]!!.num != 0
    private fun differentTile(row: Int, col: Int): Boolean = selectRow != row || selectCol != col
    private fun hoveringSomething(row: Int = selectRow, col: Int = selectCol): Boolean {
        return !(row == -1 || col == -1)
    }
    private fun notXColorTile(row: Int, col: Int, X: Color): Boolean {
        if(!hoveringSomething(row,col))
            return false
        return sudoku.getPlayingGrid()[row, col]?.getColor() != X
    }
    private fun insidePlayingField(x: Double, y: Double): Boolean {
        return x >= baseX && x <= baseX + 3 * gridS + 3 * insideBorder &&
                y >= baseY && y <= baseY + 3 * gridS + 3 * insideBorder
    }
    private fun tileIsSelected(): Boolean? {
        if(!hoveringSomething())
            return false
        return sudoku.getPlayingGrid()[selectRow,selectCol]?.selected
    }


}

private fun GraphicsContext.fillRect(Rect: Rectangle) {
    this.fillRect(Rect.x.toDouble(),Rect.y.toDouble(),Rect.width.toDouble(),Rect.height.toDouble())
}