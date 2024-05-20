package org.piepmeyer.gauguin.ui.main

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import org.piepmeyer.gauguin.ui.newgame.NewGameActivity

class NewGameContract(
    private val mainActivity: MainActivity,
) : ActivityResultContract<Unit?, NewGameContract.NewGameContractResult>() {
    override fun createIntent(
        context: Context,
        input: Unit?,
    ): Intent {
        val intent = Intent(mainActivity, NewGameActivity::class.java)

        return intent
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): NewGameContractResult {
        return if (resultCode == 99) {
            NewGameContractResult.SUCCESS
        } else {
            NewGameContractResult.ABORT
        }
    }

    enum class NewGameContractResult {
        SUCCESS,
        ABORT,
    }
}
