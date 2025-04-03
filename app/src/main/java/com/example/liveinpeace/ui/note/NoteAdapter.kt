package com.example.liveinpeace.ui.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note

class NoteAdapter(
    private val notes: MutableList<Note>, // Mutable list to allow modifications
    private val itemClickListener: OnItemClickListener,
    private val deleteClickListener: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.noteTitleTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.noteTimeTextView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteNoteButton)
    }

    interface OnItemClickListener {
        fun onItemClick(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.timeTextView.text = note.time

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(note)
        }

        holder.deleteButton.setOnClickListener {
            deleteClickListener(note) // Call delete function
        }
    }

    override fun getItemCount(): Int = notes.size

    // Function to update the note list
    fun updateList(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()  // Notify adapter that data has changed
    }
}




//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.cardview.widget.CardView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.recyclerview.widget.RecyclerView
//import com.example.liveinpeace.R
//import com.example.liveinpeace.data.Note
//
//class NoteAdapter(
//    private var notes: MutableList<Note>, // Ubah List jadi MutableList agar bisa dihapus
//    private val itemClickListener: OnItemClickListener,
//    private val deleteClickListener: (Note) -> Unit // Tambahkan parameter ini
//) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
//
//    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
//        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
//
//        //        val noteCardView: CardView = itemView.findViewById(R.id.noteCardView)
//        val titleTextView: TextView = itemView.findViewById(R.id.noteTitleTextView)
//        val timeTextView: TextView = itemView.findViewById(R.id.noteTimeTextView)
//        val rootLayout: ConstraintLayout = itemView.findViewById(R.id.noteItemRootLayout)
//        val deleteButton: ImageView = itemView.findViewById(R.id.deleteNoteButton)
//    }
//
//    interface OnItemClickListener {
//        fun onItemClick(note: Note)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
//        return NoteViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
//        val note = notes[position]
//
//        holder.dateTextView.text = note.date
//        holder.dayTextView.text = note.day
//        holder.titleTextView.text = note.title
//        holder.timeTextView.text = note.time
//
//        // Ketika item diklik
//        holder.rootLayout.setOnClickListener {
//            itemClickListener.onItemClick(note)
//        }
//
//        // Ketika tombol hapus diklik
//        holder.deleteButton.setOnClickListener {
//            deleteClickListener(note) // Panggil fungsi hapus
//        }
//    }
//
//    override fun getItemCount(): Int = notes.size
//
//    fun removeNote(note: Note) {
//        val position = notes.indexOf(note)
//        if (position != -1) {
//            notes.removeAt(position) // Bisa dihapus karena sekarang MutableList
//            notifyItemRemoved(position)
//        }
//    }
//
//    fun updateList(newList: List<Note>) {
//        notes = newList.toMutableList()
//        notifyDataSetChanged()
//    }
//}
//    fun updateList(newList: List<Note>) {
//        notes.clear()
//        notes.addAll(newList)
//        notifyDataSetChanged()
//    }




//import android.graphics.Color
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.cardview.widget.CardView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.recyclerview.widget.RecyclerView
//import com.example.liveinpeace.R
//import com.example.liveinpeace.data.Note
//import okio.blackholeSink
//
//class NoteAdapter(
//    private var notes: List<Note>,
//    private val itemClickListener: OnItemClickListener
//) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
//
//    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
//        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
//        val noteCardView: CardView = itemView.findViewById(R.id.noteCardView)
//        val titleTextView: TextView = itemView.findViewById(R.id.noteTitleTextView)
//        val timeTextView: TextView = itemView.findViewById(R.id.noteTimeTextView)
//        val rootLayout: ConstraintLayout = itemView.findViewById(R.id.noteItemRootLayout)
//    }
//
//    interface OnItemClickListener {
//        fun onItemClick(note: Note)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
//        return NoteViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
//        val note = notes[position]
//
//        holder.dateTextView.text = note.date
//        holder.dayTextView.text = note.day
//        holder.titleTextView.text = note.title
//        holder.timeTextView.text = note.time
//
//        // Set background color for the note card
//        try {
//            holder.noteCardView.setCardBackgroundColor(Color.parseColor("#000000"))
//        } catch (e: Exception) {
//            // Use default color if parsing fails
//            holder.noteCardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
//        }
//
//        // Make the entire note item clickable
//        holder.rootLayout.setOnClickListener {
//            itemClickListener.onItemClick(note)
//        }
//    }
//
//    override fun getItemCount(): Int = notes.size
//
//    fun updateList(newList: List<Note>) {
//        notes = newList
//        notifyDataSetChanged()
//    }
//}

//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.cardview.widget.CardView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.liveinpeace.R
//import com.example.liveinpeace.data.Note
//
//class NoteAdapter(
//    private var notes: List<Note>,
//    private val listener: OnItemClickListener? = null
//) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
//
//    interface OnItemClickListener {
//        fun onItemClick(note: Note)
//    }
//
//    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val titleTextView: TextView = itemView.findViewById(R.id.noteTitleTextView)
//        val contentTextView: TextView = itemView.findViewById(R.id.noteContentTextView)
//        val dateTextView: TextView = itemView.findViewById(R.id.noteDateTextView)
//        val noteCard: CardView = itemView.findViewById(R.id.noteCardView)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
//        return NoteViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
//        val note = notes[position]
//        holder.titleTextView.text = note.title
//        holder.contentTextView.text = note.content
//        holder.dateTextView.text = note.date
//
//        // Set card color based on tag
////        val cardColor = when (note.tag) {
////            "Motivasi" -> holder.itemView.context.getColor(R.color.motivasi_color)
////            "Belajar" -> holder.itemView.context.getColor(R.color.belajar_color)
////            "Kerja" -> holder.itemView.context.getColor(R.color.kerja_color)
////            else -> holder.itemView.context.getColor(R.color.default_note_color)
////        }
////        holder.noteCard.setCardBackgroundColor(cardColor)
//
//        holder.itemView.setOnClickListener {
//            listener?.onItemClick(note)
//        }
//    }
//
//    override fun getItemCount(): Int = notes.size
//
//    fun updateList(newList: List<Note>) {
//        notes = newList
//        notifyDataSetChanged()
//    }
//}