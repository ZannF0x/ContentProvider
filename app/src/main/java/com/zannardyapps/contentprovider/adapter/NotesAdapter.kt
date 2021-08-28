package com.zannardyapps.contentprovider.adapter

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zannardyapps.contentprovider.NoteClickedListener
import com.zannardyapps.contentprovider.R
import com.zannardyapps.contentprovider.database.NotesDataBaseHelper.Companion.DESCRIPTION_NOTES
import com.zannardyapps.contentprovider.database.NotesDataBaseHelper.Companion.TITLE_NOTES


class NotesAdapter(private var listener: NoteClickedListener)
    :RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private var mCursor: Cursor? = null

    @SuppressLint("NotifyDataSetChanged")
     fun setCursor(newCursor: Cursor?){
        mCursor = newCursor
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
      return NotesViewHolder(
          LayoutInflater
              .from(parent.context)
              .inflate((R.layout.note_item_layout), parent, false)
      )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        mCursor?.moveToPosition(position)

        holder.noteTitleId
            .text = mCursor?.getString(mCursor?.getColumnIndex(TITLE_NOTES) as Int)

        holder.noteDescriptionId
            .text = mCursor?.getString(mCursor?.getColumnIndex(DESCRIPTION_NOTES) as Int)

        holder.buttonRemoveId.setOnClickListener {
            mCursor?.moveToPosition(position)
            listener.noteRemoveItem(mCursor)
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            mCursor?.moveToPosition(position)
            listener.noteClickedItem(mCursor)
        }
    }

    override fun getItemCount(): Int {
        return if (mCursor != null) mCursor?.count as Int
        else 0
    }

    inner class NotesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val noteTitleId: TextView = itemView.findViewById(R.id.noteTitle)
        val noteDescriptionId: TextView = itemView.findViewById(R.id.noteDescription)
        val buttonRemoveId: Button = itemView.findViewById(R.id.buttonRemoveNote)

    }
}
