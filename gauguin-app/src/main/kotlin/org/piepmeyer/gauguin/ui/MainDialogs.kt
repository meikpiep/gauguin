package org.piepmeyer.gauguin.ui

import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.main.MainActivity
import org.piepmeyer.gauguin.ui.newgame.NewGameActivity

class MainDialogs(private val mainActivity: MainActivity) : KoinComponent {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val applicationPreferences: ApplicationPreferences by inject()

    fun newGameGridDialog() {
        val intent = Intent(mainActivity, NewGameActivity::class.java)
        val options =
            ActivityOptions.makeSceneTransitionAnimation(
                mainActivity,
                game.gridUI as View?,
                "grid",
            )
        mainActivity.startActivityForResult(intent, 0, options.toBundle())
    }

    fun restartGameDialog() {
        val builder =
            MaterialAlertDialogBuilder(
                mainActivity,
            )
        builder.setTitle(R.string.dialog_restart_current_game_title)
            .setMessage(R.string.dialog_restart_current_game_message)
            .setNegativeButton(R.string.dialog_restart_current_game_cancel_button) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.dialog_restart_current_game_ok_button) { _: DialogInterface?, _: Int ->
                gameLifecycle.restartGame()
            }
            .show()
    }

    fun openHelpDialog(deactivateNewUserFlag: Boolean = false) {
        val builder =
            MaterialAlertDialogBuilder(
                mainActivity,
            )
        val inflater = mainActivity.layoutInflater
        val layout =
            inflater.inflate(
                R.layout.dialog_help,
                mainActivity.findViewById(R.id.help_layout),
            )
        builder.setTitle(R.string.help_overall_title)
            .setView(layout)
            .setPositiveButton(R.string.help_dismiss_dialog_button) { dialog: DialogInterface, _: Int ->
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

    fun openAboutDialog(drawerLayout: MaterialDrawerSliderView) {
        val intent = Intent(mainActivity, AboutActivity::class.java)
        val options =
            ActivityOptions.makeSceneTransitionAnimation(
                mainActivity,
                drawerLayout.stickyHeaderView!!.findViewById(R.id.navigation_drawer_picture),
                "app_picture_navigation_and_about_dialog",
            )
        mainActivity.startActivityForResult(intent, 0, options.toBundle())
    }
}
