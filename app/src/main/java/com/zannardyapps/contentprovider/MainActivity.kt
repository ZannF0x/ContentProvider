package com.zannardyapps.contentprovider

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import android.widget.Button
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zannardyapps.contentprovider.adapter.NotesAdapter
import com.zannardyapps.contentprovider.database.NotesDataBaseHelper.Companion.TITLE_NOTES
import com.zannardyapps.contentprovider.database.NotesProvider.Companion.URI_NOTES
import com.zannardyapps.contentprovider.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var binding: ActivityMainBinding

    private lateinit var notesAdapter: NotesAdapter

    private lateinit var notesRecycler: RecyclerView
    private lateinit var buttonAddNotes: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initComponentsLayout()

        buttonAddNotes.setOnClickListener {
            NotesDetailFragment().show(supportFragmentManager, "dialog")
        }
    }


    // Methods Provider

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
        CursorLoader(this, URI_NOTES, null, null, null, TITLE_NOTES)

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null) {
            notesAdapter.setCursor(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        notesAdapter.setCursor(null)
    }

    // method adapter

    private fun initAdapter(){
        notesRecycler = binding.notesRecycle
        notesAdapter = NotesAdapter(object : NoteClickedListener{

            override fun noteClickedItem(cursor: Cursor?) {
                val id: Long? = cursor?.getLong(cursor.getColumnIndex(_ID))
                val fragment = NotesDetailFragment.newInstance(id as Long)
                fragment.show(supportFragmentManager, "dialog")

            }

            override fun noteRemoveItem(cursor: Cursor?) {
                val id: Long? = cursor?.getLong(cursor.getColumnIndex(_ID))

                contentResolver.delete(
                    Uri.withAppendedPath(URI_NOTES, id.toString()), null, null
                )


            }

        })

        notesAdapter.setHasStableIds(true)
        notesRecycler.layoutManager = LinearLayoutManager(this)
        notesRecycler.adapter = notesAdapter

        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    // method instance components

    private fun initComponentsLayout(){
        notesRecycler = binding.notesRecycle
        buttonAddNotes = binding.buttonAddNote
    }

}