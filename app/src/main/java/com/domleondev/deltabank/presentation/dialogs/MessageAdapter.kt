package com.domleondev.deltabank.presentation.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.domleondev.deltabank.R
import com.domleondev.deltabank.repository.geminirepository.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val RECEIVED = 0
        private const val SENT = 1
        private const val DATE = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.text.startsWith("[DATE]") -> DATE
            message.isSentByUser -> SENT
            else -> RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DATE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_separator, parent, false)
                DateViewHolder(view)
            }

            SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                SentViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                ReceivedViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder) {
            is SentViewHolder -> holder.textView.text = message.text
            is ReceivedViewHolder -> holder.textView.text = message.text
            is DateViewHolder -> {
                val dateString = message.text.removePrefix("[DATE]")

                val todayCalendar = Calendar.getInstance()
                val yesterdayCalendar =
                    Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                val todayString = dateFormat.format(todayCalendar.time)
                val yesterdayString = dateFormat.format(yesterdayCalendar.time)

                holder.dateView.text = when (dateString) {
                    todayString -> "Hoje"
                    yesterdayString -> "Ontem"
                    else -> dateString
                }
            }
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textMessage)
    }

    class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textMessage)
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateView: TextView = itemView.findViewById(R.id.tvDateSeparator)
    }
}