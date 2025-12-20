package org.piepmeyer.gauguin

import android.content.res.Resources

class DebugVariantServiceImpl(
    private val resources: Resources,
) : DebugVariantService {
    override fun isDebuggable(): Boolean = resources.getBoolean(R.bool.debuggable)
}
