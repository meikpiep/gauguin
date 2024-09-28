package org.piepmeyer.gauguin.ui.grid

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import org.piepmeyer.gauguin.R

class GridFontHolder(
    context: Context,
) {
    val fontPossibles: Typeface
    val fontCageText: Typeface
    val fontValue: Typeface

    init {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val builder = Typeface.Builder(context.assets, "font/InterVariable.ttf")

            fontPossibles = builder.setFontVariationSettings("'wght' 400").build()
            fontValue = builder.setFontVariationSettings("'wght' 425").build()
            fontCageText = builder.setFontVariationSettings("'wght' 575").build()
        } else {
            fontPossibles = checkNotNull(ResourcesCompat.getFont(context, R.font.inter_regular))
            fontValue = checkNotNull(ResourcesCompat.getFont(context, R.font.inter_regular))
            fontCageText = checkNotNull(ResourcesCompat.getFont(context, R.font.inter_semibold))
        }
    }
}
