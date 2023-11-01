package org.piepmeyer.gauguin.ui.main

import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.game.CurrentGameSaver
import org.piepmeyer.gauguin.ui.LoadGameListActivity
import org.piepmeyer.gauguin.ui.MainDialogs
import org.piepmeyer.gauguin.ui.SettingsActivity
import org.piepmeyer.gauguin.ui.StatsActivity

class MainNavigationItemSelectedListener(
    private val mainActivity: MainActivity
) :
    NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.newGame2 -> mainActivity.createNewGame()
            R.id.menu_load -> {
                val i = Intent(mainActivity, LoadGameListActivity::class.java)
                mainActivity.startActivityForResult(i, 7)
            }
            R.id.menu_save -> {
                CurrentGameSaver(mainActivity.filesDir).save()

                mainActivity.gameSaved()
            }
            R.id.menu_restart_game -> MainDialogs(mainActivity).restartGameDialog()
            R.id.menu_stats -> mainActivity.startActivity(
                Intent(
                    mainActivity,
                    StatsActivity::class.java
                )
            )
            R.id.menu_settings -> mainActivity.startActivity(
                Intent(
                    mainActivity,
                    SettingsActivity::class.java
                )
            )
            R.id.menu_help -> MainDialogs(mainActivity).openHelpDialog()
            R.id.menu_bugtracker -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://github.com/meikpiep/gauguin/issues")
                mainActivity.startActivity(intent)
            }
        }
        val drawerLayout = mainActivity.findViewById<DrawerLayout>(R.id.container)
        drawerLayout.close()
        return true
    }
}