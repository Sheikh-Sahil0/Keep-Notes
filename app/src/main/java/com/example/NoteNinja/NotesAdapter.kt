package com.example.NoteNinja

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesAdapter (private var notes : List<Note>, private val context: Context, private val listener : OnItemClickListener) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder> (){

    // mutable list of notes to add the selected note on it.
    private val selectedNotes = mutableListOf<Note>()

    class NoteViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleTextView : TextView = itemView.findViewById(R.id.txt_item_title)
        val contextTextView : TextView = itemView.findViewById(R.id.txt_item_content)
        // After designing the update activity
        // we will initialize the image view of our note item layouts editButton
        val editButton : ImageView = itemView.findViewById(R.id.img_edit_btn)
        // then we will go to onBind

        // we will initialize the ConstraintLayout of item note card views to set the background on it.
        val noteCardConstraintLayout : ConstraintLayout = itemView.findViewById(R.id.note_card_constraint_layout)
        // Declaring the pin notifier image view
        val pinNotifier : ImageView = itemView.findViewById(R.id.img_pin_notifier)
        // Declaring the note date text view
        val noteDateTextView : TextView = itemView.findViewById(R.id.txt_note_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item_layout,parent,false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position] // current note

        // it will contain all the word of the title in a list
        val titleWords = note.title.split(" ") // .split(" ") will split the line on the basis of space
        // then we will check it titleWords contains more a word then we will show just first word the title.
        holder.titleTextView.text = if (titleWords.size > 3) {
            titleWords[0] + " " + titleWords[1] + " " + titleWords[2] + "...."
        } else note.title

        holder.contextTextView.text = note.content

        // Checking if the content has more than more line of content then we will show its half
        holder.contextTextView.post {
            if (holder.contextTextView.lineCount >= 10) {
                // If the line count is more than 4 then we will show the half content
                val halfContent = note.content.substring(0, note.content.length / 2) + "...."
                holder.contextTextView.text = halfContent
            } else {
                // otherwise we will show the original content.
                holder.contextTextView.text = note.content
            }
        }

        // Whenever note appears on the screen this animation will be applied on the note
        onNoteCreateAnimation(holder)

        // Check if the note is selected (to fix the bug of auto-selecting note)
        if (selectedNotes.contains(note)) {
            holder.noteCardConstraintLayout.setBackgroundColor(Color.LTGRAY) // Selected color
        } else {
            holder.noteCardConstraintLayout.setBackgroundResource(R.drawable.green_border) // Default color
        }

        // Show pin notifier if the note is pinned
        holder.pinNotifier.visibility = if (note.isPinned == 1) View.VISIBLE else View.GONE

        // Adding the date on the note
        // Convert the timestamp into a readable date format
        val formattedDate = SimpleDateFormat("dd MMM yyy, hh:mm a", Locale.getDefault()).format(Date(note.noteDate.toLong()))
        // Set this on our view
        holder.noteDateTextView.text = formattedDate

        // Setting on click listener to the editButton
        holder.editButton.setOnClickListener{
            val updateActivityIntent = Intent(holder.itemView.context, UpdateNoteActivity ::class.java).apply {
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(updateActivityIntent)
        }
        // After that we will go to NotesDatabaseHelper class to create the updateNote function

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
            holder.noteCardConstraintLayout.setBackgroundResource(R.drawable.green_border) // and set the background
        } else { // other wise it will simply add the currently selected noted into the selectedNotes list
            selectedNotes.add(note)
            holder.noteCardConstraintLayout.setBackgroundColor(Color.LTGRAY)// set the background color to show this is selected.
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
    // Clears the selection of all selected notes
    fun clearSelection() {
        selectedNotes.clear()
        notifyDataSetChanged()
    }

    // Setting the animation when the note is created.
    private fun onNoteCreateAnimation(viewHolder : NoteViewHolder) {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.on_note_create_animation)
        viewHolder.itemView.startAnimation(slideIn)
    }

}