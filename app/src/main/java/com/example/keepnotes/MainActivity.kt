package com.example.keepnotes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.keepnotes.databinding.ActivityMainBinding

// We implemented the OnItemClickListener interface of our NotesAdapter class
class MainActivity : AppCompatActivity(), NotesAdapter.OnItemClickListener {

    // Declaring the binding feature.
    private lateinit var binding:ActivityMainBinding
    // Declaring the database
    private lateinit var db:NotesDatabaseHelper
    // Declaring the notes Adapter
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // Setting the binding feature to inflate the layout and view's
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)// setting on setContentView

        // setting the database
        db = NotesDatabaseHelper(this)

        // Setting the notes adapter
        notesAdapter = NotesAdapter(db.getAllNotes(), this,this)

        // Setting the layout manager of recycler view
        binding.notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        // Setting the adapter on our recycler view
        binding.notesRecyclerView.adapter = notesAdapter

        // Accessing floating Add button by using the binding feature and
        // setting the setOnClickListener on it.
        binding.floatingAddBtn.setOnClickListener{
            val addNoteActivityIntent = Intent(this, AddNoteActivity::class.java)
            startActivity(addNoteActivityIntent)
            // After that we will create the data class named Note
        }

        binding.btnPin.setOnClickListener {
            pinSelectedNote()
        }
    }

    // Overriding the resume methode, it will help to refresh our data whenever app resumes
    override fun onResume() {
        super.onResume()
        notesAdapter.refreshData(db.getAllNotes()) // keep refreshing the data
    }

    // Implementing the onNoteClick method from the OnItemClickListener interface
    override fun onNoteClick(note: Note) {
        // Handle single note click
    }

    // Implementing the onNoteLongClick method from the OnItemClickListener interface
    override fun onNoteLongClick(selectedNotes: List<Note>) {
        // Making pin button visible and search button invisible when notes are
        if (selectedNotes.isNotEmpty()) {
            // Check if there is exactly one note selected
            if (selectedNotes.size == 1) {
                // This if is used to toggle the icon of the pin button
                updatePinButtonIcon()
                // Make the pin button visible if there's only one selected note
                binding.btnPin.visibility = View.VISIBLE
            } else {
                // Hide the pin button if multiple notes are selected
                binding.btnPin.visibility = View.GONE
            }
            // Make the search button invisible when notes are selected
            binding.btnSearch.visibility = View.GONE
        } else {
            // Reverting visibility of buttons when no notes are selected
            binding.btnPin.visibility = View.GONE
            binding.btnSearch.visibility = View.VISIBLE
        }
    }

    private fun pinSelectedNote() {
        val selectedNotes = notesAdapter.getSelectedNotes()
        if (selectedNotes.isNotEmpty()) {
            val noteToPin = selectedNotes[0] // Get the first selected note
            // Toggle the pinned status
            noteToPin.isPinned = if (noteToPin.isPinned == 1) 0 else 1 // Change pinned status

            // Update the note in the database
            db.updateNote(noteToPin)

            // Update the pin button icon based on the new state
            updatePinButtonIcon()

            // Refresh the notes list
            notesAdapter.refreshData(db.getAllNotes())
        }
    }


    private fun updatePinButtonIcon() {
        val selectedNotes = notesAdapter.getSelectedNotes()
        val noteToPin = selectedNotes[0]
        if (noteToPin.isPinned == 1) {
            binding.btnPin.setImageResource(R.drawable.ic_unpin) // Unpinned icon
        } else {
            binding.btnPin.setImageResource(R.drawable.ic_pin) // Pinned icon
        }
    }

}