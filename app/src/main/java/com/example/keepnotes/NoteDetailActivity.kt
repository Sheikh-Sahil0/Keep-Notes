package com.example.keepnotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.keepnotes.databinding.ActivityNoteDetailBinding

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the note data passed through the intent
        val noteTitle = intent.getStringExtra("note_title")
        val noteContent = intent.getStringExtra("note_content")

        // Set the note details to the views
        binding.txtNoteTitle.text = noteTitle
        binding.txtNoteContent.text = noteContent
    }
}
