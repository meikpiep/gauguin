package com.holokenmod.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.holokenmod.R
import com.holokenmod.ui.LoadGameListAdapter.ItemClickListener
import java.io.File

class LoadGameListActivity : AppCompatActivity(), ItemClickListener {
    private var mAdapter: LoadGameListAdapter? = null
    private var empty: View? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        if (!PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("showfullscreen", false)
        ) {
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_savegame)
        empty = findViewById(android.R.id.empty)
        val recyclerView = findViewById<RecyclerView>(android.R.id.list)
        val relativeWidth = (resources.displayMetrics.widthPixels
                / resources.displayMetrics.density).toInt()
        var columns = relativeWidth / 180
        if (columns < 1) {
            columns = 1
        }
        recyclerView.layoutManager = GridLayoutManager(this, columns)
        mAdapter = LoadGameListAdapter(this)
        mAdapter!!.setClickListener(this)
        recyclerView.adapter = mAdapter
        if (mAdapter!!.itemCount == 0) {
            empty!!.visibility = View.VISIBLE
        }
        val appBar = findViewById<MaterialToolbar>(R.id.saveGameAppBar)
        appBar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.discardbutton -> {
                    deleteAllGamesDialog()
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        appBar.setNavigationOnClickListener {
            this@LoadGameListActivity.setResult(RESULT_CANCELED)
            finish()
        }
        numberOfSavedGamesChanged()
    }

    private fun deleteSaveGame(filename: File?) {
        filename!!.delete()
        mAdapter!!.refreshFiles()
        mAdapter!!.notifyDataSetChanged()
        numberOfSavedGamesChanged()
    }

    private fun deleteAllSaveGames() {
        for (file in saveGameFiles) {
            file.delete()
        }
        mAdapter!!.refreshFiles()
        mAdapter!!.notifyDataSetChanged()
        numberOfSavedGamesChanged()
    }

    private fun numberOfSavedGamesChanged() {
        if (mAdapter!!.itemCount == 0) {
            empty!!.visibility = View.VISIBLE
            findViewById<View>(R.id.discardbutton).isEnabled = false
        } else {
            empty!!.visibility = View.GONE
            findViewById<View>(R.id.discardbutton).isEnabled = true
        }
    }

    val saveGameFiles: List<File>
        get() {
            val dir = this.filesDir

            return listOf(*dir.listFiles { _: File?, name: String -> name.startsWith("savegame_") })
        }

    fun deleteGameDialog(filename: File?) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.dialog_delete_title))
            .setMessage(resources.getString(R.string.dialog_delete_msg))
            .setNegativeButton(resources.getString(R.string.dialog_cancel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(resources.getString(R.string.dialog_ok)) { _: DialogInterface?, _: Int ->
                deleteSaveGame(
                    filename
                )
            }
            .show()
    }

    private fun deleteAllGamesDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_all_title)
            .setMessage(R.string.dialog_delete_all_msg)
            .setNegativeButton(R.string.dialog_cancel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.dialog_ok) { _: DialogInterface?, _: Int -> deleteAllSaveGames() }
            .show()
    }

    fun loadSaveGame(filename: File?) {
        val i = Intent().putExtra("filename", filename!!.absolutePath)
        setResult(RESULT_OK, i)
        finish()
    }

    override fun onItemClick(view: View?, position: Int) {
        loadSaveGame(saveGameFiles[position])
    }
}