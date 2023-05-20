package com.holokenmod.ui.grid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.google.android.material.color.MaterialColors
import com.holokenmod.game.Game
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCell
import com.holokenmod.grid.GridSize
import com.holokenmod.grid.GridView
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameOptionsVariant
import com.holokenmod.options.GameVariant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

class GridUI : View, OnTouchListener, GridView, KoinComponent {
    private val game: Game by inject()

    private val cells = mutableListOf<GridCellUI>()
    private val cages = mutableListOf<GridCageUI>()

    var isSelectorShown = false
    private var gridPaint = Paint()
    private var outerBorderPaint = Paint()
    private var backgroundColor = 0
    var grid = Grid(
        GameVariant(
            GridSize(9, 9),
            GameOptionsVariant.createClassic(DigitSetting.FIRST_DIGIT_ZERO)
        ))
    private var paintHolder = GridPaintHolder(this)
    var isPreviewMode = false
    private var previewStillCalculating = false
    private var cellSizePercent = 100

    init {
        gridPaint.strokeWidth = 0f
        outerBorderPaint.strokeWidth = 3f
        outerBorderPaint.style = Paint.Style.STROKE
        outerBorderPaint.isAntiAlias = false
    }

    constructor(context: Context?) : super(context) {
        initGridView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initGridView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initGridView()
    }

    private fun initGridView() {
        setOnTouchListener(this)
    }

    fun initialize(removePencils: Boolean) {
        setOnLongClickListener { game.setSinglePossibleOnSelectedCell(removePencils) }
        isFocusable = true
        isFocusableInTouchMode = true
    }

    fun updateTheme() {
        backgroundColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface)
        outerBorderPaint.color = MaterialColors.compositeARGBWithAlpha(
            MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorOnBackground
            ), 200
        )
        gridPaint.color = MaterialColors.compositeARGBWithAlpha(
            MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorOnBackground
            ), 100
        )
        this.invalidate()
    }

    fun reCreate() {
        if (grid.gridSize.amountOfNumbers < 2) {
            return
        }
        rebuildCellsFromGrid()
        isSelectorShown = false
    }

    fun rebuildCellsFromGrid() {
        cells.clear()
        for (cell in grid.cells) {
            cells.add(GridCellUI(cell, paintHolder))
        }

        cages.clear()
        for(cage in grid.cages) {
            cages.add(GridCageUI(this, cage, paintHolder))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = measure(widthMeasureSpec)
        val measuredHeight = measure(heightMeasureSpec)
        val dim = min(measuredWidth, measuredHeight) * cellSizePercent / 100
        setMeasuredDimension(dim, dim)
    }

    private fun measure(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return if (specMode == MeasureSpec.UNSPECIFIED) {
            180
        } else {
            specSize
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(backgroundColor)

        grid.cages.forEach {
            it.setBorders()
        }

        val cellSize = cellSize.toFloat()

        drawGridLines(canvas, cellSize)

        cells.forEach {
            it.onDraw(canvas, cellSize)
        }

        cages.forEach {
            it.onDraw(canvas, cellSize)
        }

        drawGridBorders(canvas, cellSize)

        if (isPreviewMode) {
            drawPreviewMode(canvas)
        }
    }

    private fun drawGridBorders(canvas: Canvas, cellSize: Float) {
        // bottom right edge
        canvas.drawArc(
            cellSize * grid.gridSize.width - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
            cellSize * grid.gridSize.height - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
            cellSize * grid.gridSize.width + 2 + BORDER_WIDTH,
            cellSize * grid.gridSize.height + 2 + BORDER_WIDTH,
            0f,
            90f,
            false,
            outerBorderPaint
        )

        // bottom left edge
        canvas.drawArc(
            2f,
            cellSize * grid.gridSize.height - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
            2 + 2 * CORNER_RADIUS,
            cellSize * grid.gridSize.height + 2 + BORDER_WIDTH,
            90f,
            90f,
            false,
            outerBorderPaint
        )

        // top left edge
        canvas.drawArc(
            2f,
            2f,
            2 + 2 * CORNER_RADIUS,
            2 + 2 * CORNER_RADIUS,
            180f,
            90f,
            false,
            outerBorderPaint
        )

        // top right edge
        canvas.drawArc(
            cellSize * grid.gridSize.width - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
            2f,
            cellSize * grid.gridSize.width + 2 + BORDER_WIDTH,
            2 + 2 * CORNER_RADIUS,
            270f,
            90f,
            false,
            outerBorderPaint
        )

        // top
        canvas.drawLine(
            CORNER_RADIUS + 2 + BORDER_WIDTH,
            2f,
            cellSize * grid.gridSize.width - CORNER_RADIUS + 2 + BORDER_WIDTH,
            2f,
            outerBorderPaint
        )

        // bottom
        canvas.drawLine(
            CORNER_RADIUS + BORDER_WIDTH,
            cellSize * grid.gridSize.height + 2 + BORDER_WIDTH,
            cellSize * grid.gridSize.width - CORNER_RADIUS + 2 + BORDER_WIDTH,
            cellSize * grid.gridSize.height + 2 + BORDER_WIDTH,
            outerBorderPaint
        )

        // left
        canvas.drawLine(
            2f,
            CORNER_RADIUS + 2 + BORDER_WIDTH,
            2f,
            cellSize * grid.gridSize.height - CORNER_RADIUS + 2 + BORDER_WIDTH,
            outerBorderPaint
        )

        // right
        canvas.drawLine(
            cellSize * grid.gridSize.width + 2 + BORDER_WIDTH,
            CORNER_RADIUS + 2 + BORDER_WIDTH,
            cellSize * grid.gridSize.width + 2 + BORDER_WIDTH,
            cellSize * grid.gridSize.height - CORNER_RADIUS + 2 + BORDER_WIDTH,
            outerBorderPaint
        )
    }

    private fun drawPreviewMode(canvas: Canvas) {
        val previewPath = Path()
        val distanceFromEdge = resources.displayMetrics.density * 60
        val width = distanceFromEdge * 0.6f
        previewPath.moveTo(0f, distanceFromEdge + width)
        previewPath.lineTo(distanceFromEdge + width, 0f)
        previewPath.lineTo(distanceFromEdge, 0f)
        previewPath.lineTo(distanceFromEdge, 0f)
        previewPath.lineTo(0f, distanceFromEdge)
        val cageTextSize = (distanceFromEdge / 3).toInt()
        paintHolder.textOfSelectedCellPaint.textSize = cageTextSize.toFloat()
        var previewText = "Preview"
        if (previewStillCalculating) {
            previewText += "..."
        }
        canvas.drawPath(previewPath, paintHolder.mSelectedPaint)
        canvas.drawTextOnPath(
            previewText,
            previewPath,
            distanceFromEdge * 0.4f,
            distanceFromEdge * -0.08f,
            paintHolder.textOfSelectedCellPaint
        )
    }

    private val cellSize: Int
        get() {
            val cellSizeWidth = (this.measuredWidth.toFloat() - 2 * BORDER_WIDTH) / grid.gridSize.width.toFloat()
            val cellSizeHeight = (this.measuredHeight.toFloat() - 2 * BORDER_WIDTH) / grid.gridSize.height.toFloat()

            return min(cellSizeWidth, cellSizeHeight).toInt()
        }

    private fun drawGridLines(canvas: Canvas, cellSize: Float) {
        for (i in 1 until grid.gridSize.height) {
            canvas.drawLine(
                BORDER_WIDTH.toFloat(), cellSize * i + BORDER_WIDTH,
                cellSize * grid.gridSize.width, cellSize * i + BORDER_WIDTH,
                gridPaint
            )
        }
        for (i in 1 until grid.gridSize.width) {
            canvas.drawLine(
                cellSize * i + BORDER_WIDTH, BORDER_WIDTH.toFloat(),
                cellSize * i + BORDER_WIDTH, cellSize * grid.gridSize.height + BORDER_WIDTH,
                gridPaint
            )
        }
    }

    override fun onTouch(arg0: View, event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) {
            return false
        }
        if (!grid.isActive) {
            return false
        }

        try {
            val cell = getCell(event)

            isSelectorShown = true
            game.selectCell(cell)
        } catch(_: RuntimeException) {}

        return false
    }

    private fun getCell(event: MotionEvent): GridCell {
        val x = event.x
        val y = event.y
        val size = measuredWidth
        var row = ((size - (size - y)) / (size / grid.gridSize.amountOfNumbers)).toInt()
        if (row > grid.gridSize.height - 1) {
            row = grid.gridSize.height - 1
        }
        if (row < 0) {
            row = 0
        }
        var col = ((size - (size - x)) / (size / grid.gridSize.amountOfNumbers)).toInt()
        if (col > grid.gridSize.width - 1) {
            col = grid.gridSize.width - 1
        }
        if (col < 0) {
            col = 0
        }
        return grid.getCellAt(row, col)
    }

    fun setPreviewStillCalculating(previewStillCalculating: Boolean) {
        this.previewStillCalculating = previewStillCalculating
    }

    fun setCellSizePercent(cellSizePercent: Int) {
        this.cellSizePercent = cellSizePercent
    }

    companion object {
        const val CORNER_RADIUS = 15f
        const val BORDER_WIDTH = 1
    }
}