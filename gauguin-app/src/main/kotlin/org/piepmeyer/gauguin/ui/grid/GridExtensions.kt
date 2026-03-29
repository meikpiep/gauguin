package org.piepmeyer.gauguin.ui.grid

import android.content.Context
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.grid.GridSize

fun GridSize.displayableName(context: Context): String = context.getString(R.string.game_grid_size_info, this.width, this.height)
