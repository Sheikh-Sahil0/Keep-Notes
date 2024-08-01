package com.example.keepnotes

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnotes.databinding.ActivityMainBinding

class NotesAdapter (private var notes : List<Note>,private val context: Context,private val listener : OnItemClickListener) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder> (){

    private val db : NotesDatabaseHelper = NotesDatabaseHelper(context)
    private val selectedNotes = mutableListOf<Note>() // mutable list of notes to add the selected note on it.

    class NoteViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleTextView : TextView = itemView.findViewById(R.id.txt_item_title)
        val contextTextView : TextView = itemView.findViewById(R.id.txt_item_content)
        // After designing the update activity
        // we will initialize the image view of our note item layouts editButton
        val editButton : ImageView = itemView.findViewById(R.id.img_edit_btn)
        // After declaring the db we will initialize the delete btn
        val deleteButton : ImageView = itemView.findViewById(R.id.img_delete_btn)
        // then we will go to onBind

        // we will initialize the LinearLayout of item note card views to set the background on it.
        val noteCardLinearLayout : LinearLayout = itemView.findViewById(R.id.note_card_linear_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item_layout,parent,false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position] // current note
        holder.titleTextView.text = note.title
        holder.contextTextView.text = note.content

        // Check if the note is selected
        if (selectedNotes.contains(note)) {
            holder.noteCardLinearLayout.setBackgroundColor(Color.LTGRAY) // Selected color
        } else {
            holder.noteCardLinearLayout.setBackgroundResource(R.drawable.green_border) // Default color
        }

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

        // If user long-press on the particular note.
        holder.itemView.setOnLongClickListener {
            toggleSelection(note, holder)// this will pass the selected note and view holder of this note
            listener.onNoteLongClick(selectedNotes) // this listener will notifies the main activity that long click event has occurred on a note.
            true// Returning true indicates that the long click event has been consumed, and no further action should be taken.
        }

        // If user clicks on the particular note.
        holder.itemView.setOnClickListener {
            if (selectedNotes.isNotEmpty()) { // if our selectedNotes list is not empty then we will
                toggleSelection(note, holder) // pass to toggleSelection to ensure it the note selected or not
                listener.onNoteLongClick(selectedNotes)
            } else { // if our selectedNotes list is empty then we will simply start the new activity to show the details of the note.
                val detailActivityIntent = Intent(holder.itemView.context, NoteDetailActivity::class.java).apply {
                    putExtra("note_title", note.title)
                    putExtra("note_content", note.content)
                }
                holder.itemView.context.startActivity(detailActivityIntent)
            }
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

    // The toggleSelection is used check the note whether it is selected or not.
    private fun toggleSelection(note: Note, holder: NoteViewHolder) {
        if (selectedNotes.contains(note)) { // If the current note is contain inside the selectedNotes val
            selectedNotes.remove(note) // If it already exist inside the list of selectedNotes it remove it from the list
            holder.noteCardLinearLayout.setBackgroundResource(R.drawable.green_border) // and set the background
        } else { // other wise it will simply add the currently selected noted into the selectedNotes list
            selectedNotes.add(note)
            holder.noteCardLinearLayout.setBackgroundColor(Color.LTGRAY)// set the background color to show this is selected.
        }
        // Notify adapter about the change in the selected state
        notifyItemChanged(holder.adapterPosition)
    }

    interface OnItemClickListener {
        fun onNoteClick(note: Note)
        fun onNoteLongClick(selectedNotes: List<Note>)
    }

    // user get the selected notes list in mainActivity and other
    fun getSelectedNotes(): List<Note> {
        return selectedNotes
    }
}