package org.piepmeyer.gauguin.ui

import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.text.InputType
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.save.CurrentGameSaver
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.main.MainActivity
import org.piepmeyer.gauguin.ui.main.MainViewModel

class MainDialogs(
    private val mainActivity: MainActivity,
) : KoinComponent {
    private val gameLifecycle: GameLifecycle by inject()
    private val applicationPreferences: ApplicationPreferences by inject()
    private val viewModel: MainViewModel by inject()

    fun restartGameDialog() {
        viewModel
        val builder =
            MaterialAlertDialogBuilder(
                mainActivity,
            )
        builder
            .setTitle(R.string.dialog_restart_current_game_title)
            .setMessage(R.string.dialog_restart_current_game_message)
            .setNegativeButton(R.string.dialog_restart_current_game_cancel_button) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.dialog_restart_current_game_ok_button) { _: DialogInterface?, _: Int ->
                gameLifecycle.restartGame()
                viewModel.restartedGame()
            }.show()
    }

    fun saveGameWithCommentDialog(currentGameSaver: CurrentGameSaver) {
        val builder =
            MaterialAlertDialogBuilder(
                mainActivity,
            )

        val textCommentView = EditText(mainActivity)

        textCommentView.inputType = InputType.TYPE_CLASS_TEXT

        builder
            .setTitle(R.string.dialog_save_current_game_with_comment_title)
            .setMessage(R.string.dialog_save_current_game_with_comment_message)
            .setNegativeButton(
                R.string.dialog_save_current_game_with_comment_cancel_button,
            ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.dialog_save_current_game_with_comment_ok_button) { _: DialogInterface?, _: Int ->
                currentGameSaver.saveWithComment(textCommentView.text.toString())

                mainActivity.gameSaved()
            }.setView(textCommentView)
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
        builder
            .setTitle(R.string.help_overall_title)
            .setView(layout)
            .setPositiveButton(R.string.help_dismiss_dialog_button) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                if (deactivateNewUserFlag) applicationPreferences.deactivateNewUserCheck()
            }.show()
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
        mainActivity.startActivity(intent, options.toBundle())
    }
}
