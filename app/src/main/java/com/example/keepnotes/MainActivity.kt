package com.example.keepnotes

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
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
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        // Setting the setOnClickListener on it.
        binding.btnPin.setOnClickListener {
            pinSelectedNote()
        }

        // Setting the setOnClickListener on it.
        binding.btnDelete.setOnClickListener {
            deleteSelectedNotes()
        }

        // Setting the setOnClickListener on it.
        binding.btnSearch.setOnClickListener {

            if (binding.searchView.visibility == View.GONE) {
                binding.searchView.visibility = View.VISIBLE
                binding.txtNotesHeading.visibility = View.INVISIBLE
                setupSearchView()
            } else {
                binding.searchView.visibility = View.GONE
                binding.txtNotesHeading.visibility = View.VISIBLE
            }

        }

        // Setting the on click listener on filter button
        binding.imgFilterBtn.setOnClickListener {
            showFilterMenu(it)
        }

        // Register the callback for handling back presses
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Check if the SearchView is open
                if (binding.searchView.visibility == View.VISIBLE) {
                    binding.searchView.visibility = View.GONE
                    binding.txtNotesHeading.visibility = View.VISIBLE
                    return
                }
                // Check if any notes are selected
                if (notesAdapter.getSelectedNotes().isNotEmpty()) {
                    notesAdapter.clearSelection()  // Deselect the notes

                    binding.btnDelete.visibility = View.GONE
                    binding.btnPin.visibility = View.GONE
                    binding.btnSearch.visibility = View.VISIBLE
                    return
                }

                // If no conditions met, allow the back press to proceed
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        })

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
                // This is used to toggle the icon of the pin button
                updatePinButtonIcon()
                // Make the pin button visible if there's only one selected note
                binding.btnPin.visibility = View.VISIBLE
            } else {
                // Hide the pin button if multiple notes are selected
                binding.btnPin.visibility = View.GONE
            }
            // Make the search button invisible when notes are selected
            binding.btnSearch.visibility = View.GONE
            // Make the delete button visible when notes are selected
            binding.btnDelete.visibility = View.VISIBLE
            // Make the filter button invisible when notes are selected
            binding.imgFilterBtn.visibility = View.GONE

            // If search view is visible, constrain the search view to btn_pin
            if (binding.searchView.visibility == View.VISIBLE) {
                val params = binding.searchView.layoutParams as ConstraintLayout.LayoutParams
                params.endToStart = binding.btnPin.id // Constrain to btnPin
                binding.searchView.layoutParams = params
            }
        } else {
            // Reverting visibility of buttons when no notes are selected
            binding.btnPin.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE
            binding.btnSearch.visibility = View.VISIBLE
            binding.imgFilterBtn.visibility = View.VISIBLE

            // This will constraint the search view to the search again after deselecting the note
            val params = binding.searchView.layoutParams as ConstraintLayout.LayoutParams
            params.endToStart = binding.btnSearch.id // Constrain to btnSearch
            binding.searchView.layoutParams = params
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

    // Toggle the icon of the pin button(pin to unpin and wise versa)
    private fun updatePinButtonIcon() {
        val selectedNotes = notesAdapter.getSelectedNotes()
        val noteToPin = selectedNotes[0]
        if (noteToPin.isPinned == 1) {
            binding.btnPin.setImageResource(R.drawable.ic_unpin) // Unpinned icon
        } else {
            binding.btnPin.setImageResource(R.drawable.ic_pin) // Pinned icon
        }
    }

    // Delete the selected notes when the delete button is clicked
    private fun deleteSelectedNotes() {
        val selectedNotes = notesAdapter.getSelectedNotes()
        if (selectedNotes.isNotEmpty()) {
            if (selectedNotes.size == 1) {
                // Show confirmation dialog for a single note
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete this note?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        db.deleteNote(selectedNotes[0].id)
                        // Refresh the notes list after deletion
                        notesAdapter.refreshData(db.getAllNotes())
                        // Update the UI to reflect the changes
                        binding.btnDelete.visibility = View.GONE
                        binding.btnPin.visibility = View.GONE
                        binding.btnSearch.visibility = View.VISIBLE
                        Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            } else {
                // Show confirmation dialog for multiple notes
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete these notes?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        for (note in selectedNotes) {
                            db.deleteNote(note.id)
                        }
                        // Refresh the notes list after deletion
                        notesAdapter.refreshData(db.getAllNotes())
                        // Update the UI to reflect the changes
                        binding.btnDelete.visibility = View.GONE
                        binding.btnPin.visibility = View.GONE
                        binding.btnSearch.visibility = View.VISIBLE
                        Toast.makeText(this, "Notes Are Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }
    }

    // Setting the search view after clicking on the search button
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchNotes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchNotes(it) }
                return true
            }
        })
    }
    // This will actually search the note by changing or submit the text on the search view's text filed
    private fun searchNotes(query: String) {
        val filteredNotes = db.getAllNotes().filter {
            it.title.contains(query, ignoreCase = true)
        }
        notesAdapter.refreshData(filteredNotes)
    }

    // Set the drop down and handle the filter behaviour
    private fun showFilterMenu(view: View) {
        val popupLayout = layoutInflater.inflate(R.layout.popup_menu_layout, null)
        val popupWindow = PopupWindow(popupLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.isFocusable = true

        popupWindow.showAsDropDown(view)

        popupLayout.findViewById<TextView>(R.id.sort_asc).setOnClickListener {
            val db = NotesDatabaseHelper(this)
            val sortedNotes = db.getAllNotesSortedByDate("ASC")
            notesAdapter.refreshData(sortedNotes)
            popupWindow.dismiss()
        }

        popupLayout.findViewById<TextView>(R.id.sort_desc).setOnClickListener {
            val db = NotesDatabaseHelper(this)
            val sortedNotes = db.getAllNotesSortedByDate("DESC")
            notesAdapter.refreshData(sortedNotes)
            popupWindow.dismiss()
        }

        popupLayout.findViewById<TextView>(R.id.sort_default).setOnClickListener {
            val db = NotesDatabaseHelper(this)
            notesAdapter.refreshData(db.getAllNotes())
            popupWindow.dismiss()
        }
    }

}