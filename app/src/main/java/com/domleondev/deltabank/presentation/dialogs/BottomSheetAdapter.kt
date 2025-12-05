package com.domleondev.deltabank.presentation.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.domleondev.deltabank.R

class BottomSheetAdapter(
    private val items: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textItem)

        init {
            view.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
                onItemClick(items[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bottomsheet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
    }

    override fun getItemCount(): Int = items.size
}