package com.example.keepnotes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Helper class for managing the SQLite database for storing notes.
class NotesDatabaseHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "keepnotes.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
    }

    // Called when the database is created for the first time.
    override fun onCreate(db: SQLiteDatabase?) {
        // Create the allnotes table
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT)"
        db?.execSQL(createTableQuery)
    }

    // Called when the database needs to be upgraded.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop the old table and create a new one
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    // Insert the new note into the SQLite database
    fun insertNote(note: Note) {
        val db = writableDatabase // allows us for writing to the database.
        val values = ContentValues().apply { // ContentValues used to insert the values into the database
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
        }
        db.insert(TABLE_NAME, null, values) // Inserting the values into the database.
        db.close() // closing the connection
    }
    // After that we will work on the AddNoteActivity

    // We will come after completing the work of Recycler View and Item layout

    // Created this class and extends by List of our Note class
    fun getAllNotes() : List<Note> {
        val notesList = mutableListOf<Note>() // a variable which is assigned as mutable List of our Note class
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME" // created a query to select entire table
        val cursor = db.rawQuery(query,null) // to execute the above query we use rawQuery method and it stores in cursor variable
        // The cursor is used to iterate the rows through the table

        while (cursor.moveToNext()) {// cursor moves to next row
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)) // getting the id of current row
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)) // getting the title of current row
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)) // getting the content of current row

            val note = Note(id, title, content) // pass this all data to the Note class and storing it into the note val
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

        cursor.close()
        db.close()
        return Note(id, title, content)
    }

    // After that we will create a function to delete the node
    fun deleteNote (noteId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}