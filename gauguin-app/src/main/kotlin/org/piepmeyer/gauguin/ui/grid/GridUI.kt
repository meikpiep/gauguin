package org.piepmeyer.gauguin.ui.grid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.google.android.material.color.MaterialColors
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.grid.GridView
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import kotlin.math.min

class GridUI : View, OnTouchListener, GridView, KoinComponent {
    private val gridUiInjectionStrategy: GridUiInjectionStrategy = GridUiInjectionFactory.createStreategy(this)

    private val cells = mutableListOf<GridCellUI>()
    private val cages = mutableListOf<GridCageUI>()

    var isSelectorShown = false
    private var backgroundColor = 0
    override var grid = Grid(
        GameVariant(
            GridSize(9, 9),
            GameOptionsVariant.createClassic(DigitSetting.FIRST_DIGIT_ZERO)
        ))
        set(value) {
            field = value
            rebuildCellsFromGrid()
            updatePadding()
        }

    private val paintHolder = GridPaintHolder(this)
    var isPreviewMode = false
    private var previewStillCalculating = false
    private var cellSizePercent = 100
    private var padding = Pair(0, 0)

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
        val layoutDetails = GridLayoutDetails(cellSize.toFloat(), paintHolder)

        canvas.drawRoundRect(
            padding.first.toFloat(),
            padding.second.toFloat(),
            padding.first.toFloat() + cellSize * grid.gridSize.width,
            padding.second.toFloat() + cellSize * grid.gridSize.height,
            layoutDetails.gridPaintRadius(),
            layoutDetails.gridPaintRadius(),
            paintHolder.backgroundPaint())

        val cellSize = cellSize.toFloat()
        val showBadMaths = gridUiInjectionStrategy.showBadMaths()
        val fastFinishMode = gridUiInjectionStrategy.isInFastFinishingMode()
        val numeralSystem = gridUiInjectionStrategy.numeralSystem()
        val showOperators = gridUiInjectionStrategy.showOperators()
        val markDuplicatedInRowOrColumn = gridUiInjectionStrategy.markDuplicatedInRowOrColumn()

        cells.forEach {
            it.onDraw(canvas, this, cellSize, padding, layoutDetails, fastFinishMode, numeralSystem, showBadMaths, markDuplicatedInRowOrColumn)
        }

        cages.forEach {
            it.drawCageBackground(canvas, cellSize, padding, layoutDetails, showBadMaths)
            it.drawCageText(canvas, cellSize, layoutDetails, fastFinishMode, showOperators, numeralSystem)
        }

        cells.forEach {
            it.onDrawForeground(canvas, cellSize, padding, layoutDetails, fastFinishMode)
        }

        if (isPreviewMode) {
            drawPreviewBanner(canvas)
        }
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

        val previewText = if (previewStillCalculating) {
            resources.getText(R.string.new_grid_preview_banner_still_calculating)
        } else {
            resources.getText(R.string.new_grid_preview_banner_already_calculated)
        }.toString()

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

    override fun onTouch(arg0: View, event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) {
            return false
        }
        if (!grid.isActive) {
            return false
        }

        getCell(event)?.let {
            isSelectorShown = true
            gridUiInjectionStrategy.cellClicked(it)
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
        const val BORDER_WIDTH = 1
    }
}