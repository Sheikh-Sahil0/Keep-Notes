package com.example.NoteNinja

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.NoteNinja.databinding.ActivityUpdateNoteBinding

class UpdateNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper

    private var noteId:Int = -1
    // Variable to track pin status (0 for unpinned, 1 for pinned)
    private var isPinned = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        binding.btnCloseEdit.setOnClickListener {
            finish()
            Toast.makeText(this, "Note is not saved", Toast.LENGTH_SHORT).show()
        }

        val note = db.getNoteByID(noteId)
        binding.edtUpdateEditTitle.setText(note.title)
        binding.edtUpdateContent.setText(note.content)
        isPinned = note.isPinned // we updated the isPinned by getting the data of the note
        updatePinButton() // update the pin button

        // used to pin or unpin the note
        binding.btnPinEdit.setOnClickListener {
            isPinned = if (isPinned == 0) 1 else 0
            updatePinButton()
        }

        // on hit done button the data will be updated on this note
        binding.imgUpdateDoneBtn.setOnClickListener{
            saveNote()
        }
    }

    private fun updatePinButton() {
        if (isPinned == 1) {
            binding.btnPinEdit.setImageResource(R.drawable.ic_unpin)
        } else {
            binding.btnPinEdit.setImageResource(R.drawable.ic_pin)
        }
    }

    private fun saveNote() {
        val title = binding.edtUpdateEditTitle.text.toString()
        val content = binding.edtUpdateContent.text.toString()

        if (title.isNotEmpty()) {
            val updatedNote = Note(
                id = noteId, // Auto-generated by the database
                title = title,
                content = content,
                isPinned = isPinned, // Store the pin status as 0 or 1
                noteDate = System.currentTimeMillis().toString() // Store the date of note updated
            )
            db.updateNote(updatedNote)
            finish()
        } else {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}