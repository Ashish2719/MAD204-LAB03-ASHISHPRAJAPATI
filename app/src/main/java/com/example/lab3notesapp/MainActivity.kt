
/**
 * Course: MAD204
 * Lab 3: Persistent Notes App
 * Name:Ashishkumar Prajapati
 * Student ID: A00194842
 * Date: 01/11/2025
 */

package com.example.lab3notesapp

import Note
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var editTextNote: EditText
    private lateinit var buttonAddNote: Button
    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private var notesList: MutableList<Note> = mutableListOf()

    private var recentlyDeletedNote: Note? = null
    private var recentlyDeletedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextNote = findViewById(R.id.editTextNote)
        buttonAddNote = findViewById(R.id.buttonAddNote)
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)

        notesList = loadNotes().toMutableList()

        noteAdapter = NoteAdapter(notesList) { position ->
            deleteNote(position)
        }
        recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        recyclerViewNotes.adapter = noteAdapter

        buttonAddNote.setOnClickListener {
            val noteText = editTextNote.text.toString().trim()
            if (noteText.isNotEmpty()) {
                addNote(noteText)
                editTextNote.text.clear()
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addNote(text: String) {
        val note = Note(text)
        notesList.add(note)
        noteAdapter.notifyItemInserted(notesList.size - 1)
    }

    private fun deleteNote(position: Int) {
        recentlyDeletedNote = notesList[position]
        recentlyDeletedPosition = position
        notesList.removeAt(position)
        noteAdapter.notifyItemRemoved(position)

        Snackbar.make(recyclerViewNotes, "Note deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                undoDelete()
            }.show()
    }

    private fun undoDelete() {
        if (recentlyDeletedNote != null && recentlyDeletedPosition >= 0) {
            notesList.add(recentlyDeletedPosition, recentlyDeletedNote!!)
            noteAdapter.notifyItemInserted(recentlyDeletedPosition)
            recentlyDeletedNote = null
            recentlyDeletedPosition = -1
        }
    }

    override fun onPause() {
        super.onPause()
        saveNotes()
    }

    private fun saveNotes() {
        val sharedPref = getSharedPreferences("notes_pref", MODE_PRIVATE)
        with(sharedPref.edit()) {
            val json = Gson().toJson(notesList)
            putString("notes_key", json)
            apply()
        }
    }

    private fun loadNotes(): List<Note> {
        val sharedPref = getSharedPreferences("notes_pref", MODE_PRIVATE)
        val json = sharedPref.getString("notes_key", null) ?: return emptyList()
        val type = object : TypeToken<List<Note>>() {}.type
        return Gson().fromJson(json, type)
    }
}
