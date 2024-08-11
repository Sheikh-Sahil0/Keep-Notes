package com.example.keepnotes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.CaseMap.Title

// Helper class for managing the SQLite database for storing notes.
class NotesDatabaseHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "keepnotes.db"
        private const val DATABASE_VERSION = 3 // Incremented database version
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_PINNED = "isPinned" // Column for pinned status
        private const val COLUMN_NOTE_DATE = "noteDate" // Column for storing the date of the note (created or updated)
    }

    // Called when the database is created for the first time.
    override fun onCreate(db: SQLiteDatabase?) {
        // Create the all notes table
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT, $COLUMN_PINNED INTEGER DEFAULT 0, $COLUMN_NOTE_DATE TEXT)"
        db?.execSQL(createTableQuery)
    }

    // Called when the database needs to be upgraded.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1) {
            // Add the COLUMN_PINNED column if upgrading from a version is 1
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_PINNED INTEGER DEFAULT 0")
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_NOTE_DATE TEXT")
        }
        if (oldVersion == 2) {
            // Add the COLUMN_NOTE_DATE column if upgrading from a version is 2
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_NOTE_DATE TEXT")
        }
    }

    // Insert the new note into the SQLite database
    fun insertNote(note: Note) {
        val db = writableDatabase // allows us for writing to the database.
        val values = ContentValues().apply { // ContentValues used to insert the values into the database
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_PINNED, note.isPinned)
            put(COLUMN_NOTE_DATE,note.noteDate) // Storing current date
        }
        db.insert(TABLE_NAME, null, values) // Inserting the values into the database.
        db.close() // closing the connection
    }
    // After that we will work on the AddNoteActivity

    // We will come after completing the work of Recycler View and Item layout

    // Created this class and extends by List of our Note class
    // Fetch all notes from the database, ordered by pinned status and timestamp
    fun getAllNotes() : List<Note> {
        val notesList = mutableListOf<Note>() // a variable which is assigned as mutable List of our Note class to hold notes data
        val db = readableDatabase
        // Fetch notes ordered by pinned status (desc) and then by timestamp (desc)
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_PINNED DESC, $COLUMN_ID DESC" // Change to add sorting
        val cursor = db.rawQuery(query,null) // to execute the above query we use rawQuery method and it stores in cursor variable
        // The cursor is used to iterate the rows through the table

        while (cursor.moveToNext()) {// cursor moves to next row
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)) // getting the id of current row
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)) // getting the title of current row
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)) // getting the content of current row
            val isPinned = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PINNED)) // getting the pinned status of current row
            val noteDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_DATE)) // getting the date of current row

            val note = Note(id, title, content, isPinned, noteDate) // pass this all data to the Note class and storing it into the note val
            notesList.add(note) // we will add this note into notesList
        }

        cursor.close()
        db.close()
        return notesList // At last we will return the notes List
    }
    // After that we will create the adapter for recycler view

    // Now we will create the updateNote and getNoteByID function
    fun updateNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_PINNED, note.isPinned)
            put(COLUMN_NOTE_DATE, note.noteDate) // Update the current on the note
        }

        val whereClause = "$COLUMN_ID = ?" // this will used to update the row by its column id
        val whereArgs = arrayOf(note.id.toString())

        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }
    // This function is used in UpdateNoteActivity to update the note by finding the note through its ID
    fun getNoteByID (noteID : Int) : Note {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $noteID"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val isPinned = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PINNED))
        val noteDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_DATE))

        cursor.close()
        db.close()
        return Note(id, title, content, isPinned, noteDate)
    }

    // After that we will create a function to delete the node
    fun deleteNote (noteId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun getAllNotesSortedByDate(order: String): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = this.readableDatabase

        // Order by pinned notes first, then by date
        val orderBy = "$COLUMN_PINNED DESC, $COLUMN_NOTE_DATE $order"

        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            orderBy
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val isPinned = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PINNED))
                val noteDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_DATE))

                val note = Note(id, title, content, isPinned, noteDate)
                notesList.add(note)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return notesList
    }

}