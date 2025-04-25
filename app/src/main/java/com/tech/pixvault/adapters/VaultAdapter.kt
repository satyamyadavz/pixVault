package com.tech.pixvault.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tech.pixvault.R
import java.io.File

class VaultAdapter(
    private val files: List<File>,
    private val context: Context,
    private val onItemClick: (File) -> Unit
) : RecyclerView.Adapter<VaultAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileIcon: ImageView = view.findViewById(R.id.fileIcon)
        val fileName: TextView = view.findViewById(R.id.fileNameText)

        init {
            view.setOnClickListener {
                val file = files[adapterPosition]
                onItemClick(file)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vault_file, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        holder.fileName.text = file.name

        if (file.name.endsWith(".jpg") || file.name.endsWith(".png") || file.name.endsWith(".jpeg")) {
            Glide.with(context).load(file).into(holder.fileIcon)
        } else if (file.name.endsWith(".mp4")) {
            Glide.with(context).load(file).thumbnail(0.1f).into(holder.fileIcon)
        } else {
            holder.fileIcon.setImageResource(R.drawable.ic_file_generic)
        }
    }
}
