package org.piepmeyer.gauguin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.game.save.SaveGame
import org.piepmeyer.gauguin.game.save.SavedGamesService
import org.piepmeyer.gauguin.ui.grid.GridUI
import java.io.File
import java.text.DateFormat

private val logger = KotlinLogging.logger {}

class LoadGameListAdapter(
    private val context: LoadGameListActivity,
) : RecyclerView.Adapter<LoadGameListAdapter.ViewHolder>(),
    KoinComponent {
    private val savedGamesService: SavedGamesService by inject()

    private val mGameFiles = mutableListOf<File>()
    private val inflater = LayoutInflater.from(context)

    init {
        refreshFiles()
    }

    fun refreshFiles() {
        mGameFiles.clear()
        mGameFiles.addAll(savedGamesService.savedGameFiles())
        mGameFiles.sortWith(SortSavedGames())
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = inflater.inflate(R.layout.view_savegame, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val saveFile = mGameFiles[position]
        val saver = SaveGame.createWithFile(saveFile)
        try {
            val grid = saver.restore()
            grid?.let {
                holder.gridUI.grid = it
                holder.gridUI.rebuildCellsFromGrid()
                it.isActive = false
            }
        } catch (e: Exception) {
            // Error, delete the file.
            // saveFile.delete()
            logger.error(e) { "Could not load game from file ${saveFile.name}, error message: ${e.message}" }
            return
        }
        val grid = holder.gridUI.grid
        holder.gridUI.updateTheme()
        for (cell in grid.cells) {
            cell.isSelected = false
        }
        holder.duration.text = Utils.displayableGameDuration(grid.playTime)

        grid.description?.let {
            holder.description.text = it
        }
        holder.description.visibility = if (grid.description == null) View.INVISIBLE else View.VISIBLE

        holder.gametitle.text =
            context.getString(R.string.game_grid_size_info, grid.gridSize.width, grid.gridSize.height)
        holder.date.text =
            DateFormat.getDateInstance(DateFormat.MEDIUM).format(
                grid.creationDate,
            )
        holder.time.text =
            DateFormat.getTimeInstance(DateFormat.SHORT).format(
                grid.creationDate,
            )
        holder.loadButton.setOnClickListener { context.loadSaveGame(saveFile) }
        holder.deleteButton.setOnClickListener { context.deleteGameDialog(saveFile) }
    }

    // total number of rows
    override fun getItemCount(): Int = mGameFiles.size

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val gridUI: GridUI = itemView.findViewById(R.id.saveGridView)
        val gametitle: TextView = itemView.findViewById(R.id.saveGameTitle)
        val date: TextView = itemView.findViewById(R.id.saveDate)
        val time: TextView = itemView.findViewById(R.id.saveTime)
        val description: TextView = itemView.findViewById(R.id.save_game_description)
        val duration: TextView = itemView.findViewById(R.id.saveGameDuration)
        val loadButton: MaterialButton = itemView.findViewById(R.id.button_play)
        val deleteButton: MaterialButton = itemView.findViewById(R.id.button_delete)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            loadButton.callOnClick()
        }
    }

    private class SortSavedGames : Comparator<File> {
        override fun compare(
            object1: File,
            object2: File,
        ): Int = -1 * object1.name.compareTo(object2.name)
    }
}
