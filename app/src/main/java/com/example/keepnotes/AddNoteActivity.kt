package com.example.keepnotes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.keepnotes.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    // Declare the database from the notesDatabaseHelper class
    private lateinit var db : NotesDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the db
        db = NotesDatabaseHelper(this)

        // Storing the data by on clicking the done button
        binding.imgDoneBtn.setOnClickListener{
            val title = binding.edtEditTitle.text.toString()
            val content = binding.edtContent.text.toString()
            val note = Note(0, title,content,0)
            db.insertNote(note)
            finish()
            Toast.makeText(this, "Note saved.",Toast.LENGTH_SHORT).show()
        }
    }
}