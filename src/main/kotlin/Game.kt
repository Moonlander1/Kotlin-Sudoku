import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.stage.Stage

class Game : Application() {

    companion object {
        private const val WIDTH  = 650
        private const val HEIGHT = 650
    }
    private lateinit var mainScene: Scene
    private lateinit var batch: GraphicsContext
    private lateinit var GUI: SudokuGUI


    /* Initializing */
    override fun start(mainStage: Stage) {
        mainStage.title = "Sudoku"
        val root = Group()
        val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())

        root.children.add(canvas)
        mainScene = Scene(root)
        batch = canvas.graphicsContext2D
        GUI = SudokuGUI(batch)
        mainStage.scene = mainScene

        prepareActionHandlers()

        // Main loop
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender(currentNanoTime)
            }
        }.start()

        mainStage.show()
    }

    /* Preparing input handlers */
    private fun prepareActionHandlers() {
        mainScene.onKeyPressed    = EventHandler { event ->
            GUI.keyTyped(event)
        }
        mainScene.onMouseMoved    = EventHandler { event ->
            GUI.mouseMovement(event)
        }
        mainScene.onMousePressed  = EventHandler { event ->
            GUI.mousePressed(event)
        }
        mainScene.onMouseReleased = EventHandler { event ->
            GUI.mouseReleased(event)
        }
    }

    /* Main game loop */
    private fun tickAndRender(currentNanoTime: Long) {

        // clear canvas
        batch.clearRect(0.0,0.0,WIDTH.toDouble(), HEIGHT.toDouble())
        batch.fill = Color.rgb(139,123,153)
        batch.fillRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        // draw background
        GUI.update()
        // perform world updates

    }
}

