package org.piepmeyer.gauguin.ui

import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.options.ApplicationPreferences
import org.piepmeyer.gauguin.ui.main.MainActivity
import org.piepmeyer.gauguin.ui.newgame.NewGameActivity

class MainDialogs(private val mainActivity: MainActivity): KoinComponent {
    private val game: Game by inject()
    private val applicationPreferences: ApplicationPreferences by inject()

    fun newGameGridDialog() {
        val intent = Intent(mainActivity, NewGameActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            mainActivity,
            game.gridUI as View?,
            "grid"
        )
        mainActivity.startActivityForResult(intent, 0, options.toBundle())
    }

    fun restartGameDialog() {
        if (!game.grid.isActive) {
            return
        }
        val builder = MaterialAlertDialogBuilder(
            mainActivity
        )
        builder.setTitle(R.string.dialog_restart_title)
            .setMessage(R.string.dialog_restart_msg)
            .setNegativeButton(R.string.dialog_cancel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.dialog_ok) { _: DialogInterface?, _: Int ->
                game.restartGame()
                mainActivity.startFreshGrid(true)
            }
            .show()
    }

    fun openHelpDialog(deactivateNewUserFlag: Boolean = false) {
        val builder = MaterialAlertDialogBuilder(
            mainActivity
        )
        val inflater = mainActivity.layoutInflater
        val layout = inflater.inflate(
            R.layout.dialog_help,
            mainActivity.findViewById(R.id.help_layout)
        )
        builder.setTitle(R.string.help_section_title)
            .setView(layout)
            .setNeutralButton(R.string.about_section_title) { _: DialogInterface?, _: Int ->
                openAboutDialog()
                if (deactivateNewUserFlag) applicationPreferences.deactivateNewUserCheck()
            }
            .setPositiveButton(R.string.dialog_ok) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                if (deactivateNewUserFlag) applicationPreferences.deactivateNewUserCheck()
            }
            .show()
    }

    fun openNewUserHelpDialog() {
        if (applicationPreferences.newUserCheck()) {
            openHelpDialog(true)
        }
    }

    private fun openAboutDialog() {
        val builder = MaterialAlertDialogBuilder(
            mainActivity
        )
        val inflater = LayoutInflater.from(mainActivity)
        val layout = inflater.inflate(
            R.layout.dialog_about,
            mainActivity.findViewById(R.id.about_layout)
        )
        builder.setTitle(R.string.about_section_title)
            .setView(layout)
            .setNeutralButton(R.string.help_section_title) { _: DialogInterface?, _: Int -> openHelpDialog() }
            .setPositiveButton(R.string.dialog_ok) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .show()
    }
}