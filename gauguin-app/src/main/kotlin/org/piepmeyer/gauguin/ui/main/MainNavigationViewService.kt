package org.piepmeyer.gauguin.ui.main

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.iconRes
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.util.updateItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityMainBinding
import org.piepmeyer.gauguin.game.save.CurrentGameSaver
import org.piepmeyer.gauguin.game.save.SavedGamesListener
import org.piepmeyer.gauguin.game.save.SavedGamesService
import org.piepmeyer.gauguin.ui.LoadGameListActivity
import org.piepmeyer.gauguin.ui.MainDialogs
import org.piepmeyer.gauguin.ui.SettingsActivity
import org.piepmeyer.gauguin.ui.statistics.StatisticsActivity

class MainNavigationViewService(
    private val mainActivity: MainActivity,
    private val binding: ActivityMainBinding,
) : KoinComponent {
    private val savedGamesService: SavedGamesService by inject()
    private val currentGameSaver: CurrentGameSaver by inject()

    fun initialize() {
        binding.container.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        val newGameItem =
            PrimaryDrawerItem().apply {
                nameRes = R.string.main_menu_item_new
                identifier = 1
                iconRes = R.drawable.outline_add_24
            }
        val restartGameItem =
            PrimaryDrawerItem().apply {
                nameRes = R.string.main_menu_item_restart_game
                identifier = 2
                iconRes = R.drawable.outline_replay_24
            }
        val loadGameItem =
            PrimaryDrawerItem().apply {
                nameRes = R.string.main_menu_item_load_game
                identifier = 3
                iconRes = R.drawable.outline_open_in_new_24
            }
        val saveGameItem =
            PrimaryDrawerItem().apply {
                nameRes = R.string.main_menu_item_save_game
                identifier = 4
                iconRes = R.drawable.outline_save_24
            }
        val statisticsItem =
            SecondaryDrawerItem().apply {
                nameRes = R.string.main_menu_item_show_statistics
                identifier = 5
                iconRes = R.drawable.outline_leaderboard_24
            }
        val settingsItem =
            SecondaryDrawerItem().apply {
                nameRes = R.string.main_menu_item_open_settings
                identifier = 6
                iconRes = R.drawable.outline_settings_24
            }
        val helpItem =
            SecondaryDrawerItem().apply {
                nameRes = R.string.main_menu_item_show_help
                identifier = 7
                iconRes = R.drawable.outline_help_24
            }
        val bugsAndFeaturesItem =
            SecondaryDrawerItem().apply {
                nameRes = R.string.main_menu_item_open_github_issues
                identifier = 8
                iconRes = R.drawable.outline_bug_report_24
            }

        val savedGamesListener =
            SavedGamesListener {
                val countOfSavedGames = savedGamesService.countOfSavedGames()

                if (countOfSavedGames > 0) {
                    loadGameItem.badge = StringHolder(countOfSavedGames.toString())
                } else {
                    loadGameItem.badge = null
                }

                binding.mainNavigationView.updateItem(loadGameItem)
            }

        savedGamesService.addSavedGamesListener(savedGamesListener)
        savedGamesListener.savedGamesChanged()

        binding.mainNavigationView.itemAdapter.add(
            newGameItem,
            restartGameItem,
            DividerDrawerItem(),
            loadGameItem,
            saveGameItem,
            DividerDrawerItem(),
            statisticsItem,
            settingsItem,
            helpItem,
            bugsAndFeaturesItem,
        )

        val header =
            View.inflate(
                ContextThemeWrapper(
                    binding.mainNavigationView.context,
                    R.style.AppTheme,
                ),
                R.layout.view_main_navigation_drawer_header,
                null,
            )

        binding.mainNavigationView.stickyHeaderView = header
        header.setBackgroundResource(0)

        binding.mainNavigationView.onDrawerItemClickListener = { _, menuItem, _ ->
            when (menuItem) {
                newGameItem -> mainActivity.createNewGame()
                loadGameItem -> {
                    val i = Intent(mainActivity, LoadGameListActivity::class.java)
                    mainActivity.startActivityForResult(i, 7)
                }
                saveGameItem -> {
                    currentGameSaver.save()

                    mainActivity.gameSaved()
                }
                restartGameItem -> MainDialogs(mainActivity).restartGameDialog()
                statisticsItem ->
                    mainActivity.startActivity(
                        Intent(
                            mainActivity,
                            StatisticsActivity::class.java,
                        ),
                    )
                settingsItem ->
                    mainActivity.startActivity(
                        Intent(
                            mainActivity,
                            SettingsActivity::class.java,
                        ),
                    )
                helpItem -> MainDialogs(mainActivity).openHelpDialog()
                bugsAndFeaturesItem -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://github.com/meikpiep/gauguin/issues")
                    mainActivity.startActivity(intent)
                }
            }
            val drawerLayout = mainActivity.findViewById<DrawerLayout>(R.id.container)
            drawerLayout.close()

            binding.mainNavigationView.selectExtension.deselect()

            true
        }

        binding.mainBottomAppBar.setOnMenuItemClickListener(
            BottomAppBarItemClickListener(mainActivity),
        )
        binding.mainBottomAppBar.setNavigationOnClickListener { binding.container.open() }

        binding.gridview.addOnLayoutChangeListener { _, _, _, right, _, _, _, _, _ ->
            if (binding.mainBottomAppBar.marginStart != 0 && right > 0 && binding.mainBottomAppBar.marginStart != right) {
                val marginParams =
                    binding.mainBottomAppBar.layoutParams as ViewGroup.MarginLayoutParams
                marginParams.marginStart = right

                binding.mainBottomAppBar.updateLayoutParams<ViewGroup.MarginLayoutParams> { }
                binding.container.invalidate()
            }
        }
    }
}
