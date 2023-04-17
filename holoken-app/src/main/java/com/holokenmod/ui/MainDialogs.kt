package com.holokenmod.ui

import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.holokenmod.R
import com.holokenmod.game.Game
import com.holokenmod.ui.main.MainActivity
import com.holokenmod.ui.newgame.NewGameActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainDialogs(private val mainActivity: MainActivity): KoinComponent {
    private val game: Game by inject()

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
        val builder = AlertDialog.Builder(
            mainActivity
        )
        builder.setTitle(R.string.dialog_restart_title)
            .setMessage(R.string.dialog_restart_msg)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNegativeButton(R.string.dialog_cancel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.dialog_ok) { _: DialogInterface?, _: Int ->
                game.restartGame()
                mainActivity.startFreshGrid(true)
            }
            .show()
    }

    fun openHelpDialog() {
        val builder = AlertDialog.Builder(
            mainActivity
        )
        val inflater = LayoutInflater.from(mainActivity)
        val layout = inflater.inflate(
            R.layout.dialog_help,
            mainActivity.findViewById(R.id.help_layout)
        )
        builder.setTitle(R.string.help_section_title)
            .setView(layout)
            .setNeutralButton(R.string.about_section_title) { _: DialogInterface?, _: Int -> openAboutDialog() }
            .setPositiveButton(R.string.dialog_ok) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .show()
    }

    private fun openAboutDialog() {
        val builder = AlertDialog.Builder(
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