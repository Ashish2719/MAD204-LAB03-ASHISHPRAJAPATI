package com.example.lab3notesapp

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class NoteAdapter(
    private val notes: MutableList<ContactsContract.CommonDataKinds.Note>,
    private val onLongClick: (Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNote: TextView = itemView.findViewById(R.id.textViewNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.textViewNote.text = notes[position].text
        holder.itemView.setOnLongClickListener {
            onLongClick(position)
            true
        }
    }

    override fun getItemCount(): Int = notes.size
}
