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
    override var grid = Grid(
        GameVariant(
            GridSize(9, 9),
            GameOptionsVariant.createClassic(DigitSetting.FIRST_DIGIT_ZERO)
        ))
        set(value) {
            field = value
            rebuildCellsFromGrid()
        }

    private var paintHolder = GridPaintHolder(this)
    var isPreviewMode = false
    private var previewStillCalculating = false
    private var cellSizePercent = 100
    private var padding = Pair(0, 0)

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
        isFocusable = true
        isFocusableInTouchMode = true
    }

    fun updateTheme() {
        backgroundColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface)
        outerBorderPaint.color = MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary)

        //gridPaint.color = MaterialColors.getColor(this, com.google.android.material.R.attr.colorTertiary)

        gridPaint.color = MaterialColors.compositeARGBWithAlpha(
            MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorSecondary
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
        setMeasuredDimension(
            measuredWidth * cellSizePercent / 100,
            measuredHeight * cellSizePercent / 100
        )
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        updatePadding()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        updatePadding()
    }

    private fun updatePadding() {
        padding = Pair(
            (width - (cellSize * grid.gridSize.width)) / 2 + (width - measuredWidth) / 2,
            (height - (cellSize * grid.gridSize.height)) / 2 + (height - measuredHeight) / 2
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(backgroundColor)

        grid.cages.forEach {
            it.setBorders()
        }

        val cellSize = cellSize.toFloat()

        drawGridLines(canvas, cellSize)

        cells.forEach {
            it.onDraw(canvas, cellSize, padding)
        }

        cages.forEach {
            it.onDraw(canvas, cellSize, padding)
        }

        drawGridBorders(canvas, cellSize)

        if (isPreviewMode) {
            drawPreviewBanner(canvas)
        }
    }

    private fun drawGridBorders(canvas: Canvas, cellSize: Float) {
        // bottom right edge
        canvas.drawArc(
            padding.first + cellSize * grid.gridSize.width - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
            padding.second + cellSize * grid.gridSize.height - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
            padding.first + cellSize * grid.gridSize.width + 2 + BORDER_WIDTH,
            padding.second + cellSize * grid.gridSize.height + 2 + BORDER_WIDTH,
            0f,
            90f,
            false,
            outerBorderPaint
        )

        // bottom left edge
        canvas.drawArc(
            padding.first + 2f,
            padding.second + cellSize * grid.gridSize.height - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
            padding.first + 2 + 2 * CORNER_RADIUS,
            padding.second + cellSize * grid.gridSize.height + 2 + BORDER_WIDTH,
            90f,
            90f,
            false,
            outerBorderPaint
        )

        // top left edge
        canvas.drawArc(
            padding.first + 2f,
            padding.second + 2f,
            padding.first + 2 + 2 * CORNER_RADIUS,
            padding.second + 2 + 2 * CORNER_RADIUS,
            180f,
            90f,
            false,
            outerBorderPaint
        )

        // top right edge
        canvas.drawArc(
            padding.first + cellSize * grid.gridSize.width - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
            padding.second + 2f,
            padding.first + cellSize * grid.gridSize.width + 2 + BORDER_WIDTH,
            padding.second + 2 + 2 * CORNER_RADIUS,
            270f,
            90f,
            false,
            outerBorderPaint
        )

        // top
        canvas.drawLine(
            padding.first + CORNER_RADIUS + 2 + BORDER_WIDTH,
            padding.second + 2f,
            padding.first + cellSize * grid.gridSize.width - CORNER_RADIUS + 2 + BORDER_WIDTH,
            padding.second + 2f,
            outerBorderPaint
        )

        // bottom
        canvas.drawLine(
            padding.first + CORNER_RADIUS + BORDER_WIDTH,
            padding.second + cellSize * grid.gridSize.height + 2 + BORDER_WIDTH,
            padding.first + cellSize * grid.gridSize.width - CORNER_RADIUS + 2 + BORDER_WIDTH,
            padding.second + cellSize * grid.gridSize.height + 2 + BORDER_WIDTH,
            outerBorderPaint
        )

        // left
        canvas.drawLine(
            padding.first + 2f,
            padding.second + CORNER_RADIUS + 2 + BORDER_WIDTH,
            padding.first + 2f,
            padding.second + cellSize * grid.gridSize.height - CORNER_RADIUS + 2 + BORDER_WIDTH,
            outerBorderPaint
        )

        // right
        canvas.drawLine(
            padding.first + cellSize * grid.gridSize.width + 2 + BORDER_WIDTH,
            padding.second + CORNER_RADIUS + 2 + BORDER_WIDTH,
            padding.first + cellSize * grid.gridSize.width + 2 + BORDER_WIDTH,
            padding.second + cellSize * grid.gridSize.height - CORNER_RADIUS + 2 + BORDER_WIDTH,
            outerBorderPaint
        )
    }

    private fun drawPreviewBanner(canvas: Canvas) {
        val previewPath = Path()
        val distanceFromEdge = resources.displayMetrics.density * 60
        val width = distanceFromEdge * 0.6f
        previewPath.moveTo(0f, distanceFromEdge + width)
        previewPath.lineTo(distanceFromEdge + width, 0f)
        previewPath.lineTo(distanceFromEdge, 0f)
        previewPath.lineTo(distanceFromEdge, 0f)
        previewPath.lineTo(0f, distanceFromEdge)
        val cageTextSize = (distanceFromEdge / 3).toInt()

        val textPaint = paintHolder.previewBannerTextPaint()
        textPaint.textSize = cageTextSize.toFloat()

        var previewText = "Preview"
        if (previewStillCalculating) {
            previewText += "..."
        }
        previewPath.offset(padding.first.toFloat(), padding.second.toFloat())

        canvas.drawPath(previewPath, paintHolder.previewBannerBackgroundPaint())
        canvas.drawTextOnPath(
            previewText,
            previewPath,
            distanceFromEdge * 0.4f,
            distanceFromEdge * -0.08f,
            textPaint
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
                padding.first + BORDER_WIDTH.toFloat(), padding.second + cellSize * i + BORDER_WIDTH,
                padding.first + cellSize * grid.gridSize.width, padding.second + cellSize * i + BORDER_WIDTH,
                gridPaint
            )
        }
        for (i in 1 until grid.gridSize.width) {
            canvas.drawLine(
                padding.first + cellSize * i + BORDER_WIDTH, padding.second + BORDER_WIDTH.toFloat(),
                padding.first + cellSize * i + BORDER_WIDTH, padding.second + cellSize * grid.gridSize.height + BORDER_WIDTH,
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

        getCell(event)?.let {
            isSelectorShown = true
            game.selectCell(it)
        }

        return false
    }

    private fun getCell(event: MotionEvent): GridCell? {
        val x = event.x - padding.first
        val y = event.y - padding.second

        if ( x < 0 || y < 0)
            return null

        val row = (y / cellSize).toInt()
        if (row > grid.gridSize.height - 1) {
            return null
        }

        val col = (x / cellSize).toInt()
        if (col > grid.gridSize.width - 1) {
            return null
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