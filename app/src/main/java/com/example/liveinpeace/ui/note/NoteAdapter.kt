package com.example.liveinpeace.ui.note

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(
    private var notes: MutableList<Note>,
    private val onItemClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val titleTextView: TextView = itemView.findViewById(R.id.noteTitleTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.noteTimeTextView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteNoteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        holder.titleTextView.text = note.title
        holder.dateTextView.text = note.date
        holder.timeTextView.text = note.time

        // Tentukan nama hari berdasarkan tanggal
        holder.dayTextView.text = getDayName(note.date)

        holder.itemView.setOnClickListener {
            onItemClick(note)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(note)
        }
    }

    override fun getItemCount(): Int = notes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    private fun getDayName(dateString: String): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(dateString)
            val dayFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))
            dayFormat.format(date ?: Date()).uppercase(Locale("id", "ID"))
        } catch (e: Exception) {
            e.printStackTrace()
            return "Tidak diketahui"
        }
    }
}