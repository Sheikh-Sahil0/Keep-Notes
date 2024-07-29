package com.example.keepnotes

// This is a data class used to hold the data of our notes like id, title and the content of our notes
data class Note(val id : Int, val title : String, val content : String)

// After that we will create the helper class for helping to store the data in this class
// Named as NotesDatabaseHelper