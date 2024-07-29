package com.example.keepnotes

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter (private var notes : List<Note>, context: Context) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder> (){

    private val db : NotesDatabaseHelper = NotesDatabaseHelper(context)

    class NoteViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleTextView : TextView = itemView.findViewById(R.id.txt_item_title)
        val contextTextView : TextView = itemView.findViewById(R.id.txt_item_content)
        // After designing the update activity
        // we will initialize the image view of our note item layouts editButton
        val editButton : ImageView = itemView.findViewById(R.id.img_edit_btn)
        // After declaring the db we will initialize the delete btn
        val deleteButton : ImageView = itemView.findViewById(R.id.img_delete_btn)
        // then we will go to onBind
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item_layout,parent,false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contextTextView.text = note.content

        // Setting on click listener to the editButton
        holder.editButton.setOnClickListener{
            val updateActivityIntent = Intent(holder.itemView.context, UpdateNoteActivity ::class.java).apply {
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(updateActivityIntent)
        }
        // After that we will go to NotesDatabaseHelper class to create the updateNote function
        // We are here to set the delete button
        holder.deleteButton.setOnClickListener {
            // Show confirmation dialog
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setMessage("Are you sure you want to delete this note?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    db.deleteNote(note.id)
                    refreshData(db.getAllNotes())
                    Toast.makeText(holder.itemView.context, "Note Deleted", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        // Setting click listener to the entire item view
        holder.itemView.setOnClickListener {
            val detailActivityIntent = Intent(holder.itemView.context, NoteDetailActivity::class.java).apply {
                putExtra("note_title", note.title)
                putExtra("note_content", note.content)
            }
            holder.itemView.context.startActivity(detailActivityIntent)
        }
    }

    // we created this function to refresh the data the data or update the the notes list
    fun refreshData (newNotes : List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    // After that we will go to main activity to set up this adapter
    // After creating the function to delete the note we will declare db on the top of this class
    // we have declared the db because we want to access the deleteNote function from NotesDatabaseHelper class
}