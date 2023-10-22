package org.piepmeyer.gauguin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.game.SaveGame.Companion.createWithFile
import org.piepmeyer.gauguin.ui.grid.GridUI
import java.io.File
import java.text.DateFormat
import kotlin.math.sign

class LoadGameListAdapter(context: LoadGameListActivity) :
    RecyclerView.Adapter<LoadGameListAdapter.ViewHolder>() {
    private val mGameFiles: MutableList<File>
    private val inflater: LayoutInflater
    private var clickListener: ItemClickListener? = null
    private val mContext: LoadGameListActivity

    init {
        inflater = LayoutInflater.from(context)
        mContext = context
        mGameFiles = mutableListOf()
        refreshFiles()
    }

    fun refreshFiles() {
        mGameFiles.clear()
        mGameFiles.addAll(mContext.saveGameFiles)
        mGameFiles.sortWith(SortSavedGames())
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.view_savegame, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val saveFile = mGameFiles[position]
        val saver = createWithFile(saveFile)
        try {
            val grid = saver.restore()
            grid?.let {
                holder.gridUI.grid = it
                holder.gridUI.rebuildCellsFromGrid()
                it.isActive = false
            }
        } catch (e: Exception) {
            // Error, delete the file.
            saveFile.delete()
            return
        }
        val grid = holder.gridUI.grid
        holder.gridUI.updateTheme()
        for (cell in grid.cells) {
            cell.isSelected = false
        }
        holder.duration.text = Utils.displayableGameDuration(grid.playTime)
        holder.gametitle.text = grid.gridSize.toString()
        holder.date.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(
            grid.creationDate
        )
        holder.time.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(
            grid.creationDate
        )
        holder.loadButton.setOnClickListener { mContext.loadSaveGame(saveFile) }
        holder.deleteButton.setOnClickListener { mContext.deleteGameDialog(saveFile) }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mGameFiles.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val gridUI: GridUI
        val gametitle: TextView
        val date: TextView
        val time: TextView
        val duration: TextView
        val loadButton: MaterialButton
        val deleteButton: MaterialButton

        init {
            gridUI = itemView.findViewById(R.id.saveGridView)
            gametitle = itemView.findViewById(R.id.saveGameTitle)
            date = itemView.findViewById(R.id.saveDate)
            time = itemView.findViewById(R.id.saveTime)
            duration = itemView.findViewById(R.id.saveGameDuration)
            loadButton = itemView.findViewById(R.id.button_play)
            deleteButton = itemView.findViewById(R.id.button_delete)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            clickListener?.onItemClick(view, absoluteAdapterPosition)
        }
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    fun interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    private class SortSavedGames : Comparator<File> {
        var save1: Long = 0
        var save2: Long = 0
        override fun compare(object1: File, object2: File): Int {
            try {
                save1 = createWithFile(object1).readDate()
                save2 = createWithFile(object2).readDate()
            } catch (e: Exception) {
                //
            }
            return sign((save2 - save1).toFloat()).toInt()
        }
    }
}