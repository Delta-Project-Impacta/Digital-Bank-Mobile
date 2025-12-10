package com.domleondev.deltabank.presentation.dialogs

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.domleondev.deltabank.R
import com.domleondev.deltabank.repository.request.TransactionRequest
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ExtractAdapter(private val list: List<TransactionRequest>) :
    RecyclerView.Adapter<ExtractAdapter.ExtractViewHolder>() {

    class ExtractViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.extract_date)
        val tvType: TextView = view.findViewById(R.id.extract_Transaction_View)
        val tvDescription: TextView = view.findViewById(R.id.extract_Transaction_Description)
        val tvAmount: TextView = view.findViewById(R.id.extract_Transaction_Value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtractViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.extract_day_item, parent, false)

        Log.d("EXTRACT_ADAPTER", "onCreateViewHolder — item inflado")
        return ExtractViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExtractViewHolder, position: Int) {
        val item = list[position]

        Log.d("EXTRACT_ADAPTER", "Bind -> $position | ${item.type} | ${item.amount} | ${item.timestamp}")

        // TYPE
        holder.tvType.text = when (item.type) {
            "pix_received" -> "Pix recebido"
            "pix_sent" -> "Pix enviado"
            "card_purchase" -> "Compra com o Cartão"
            "deposit" -> "Depósito"
            else -> item.type
        }

        // DESCRIPTION
        holder.tvDescription.text = item.description

        // AMOUNT
        val formattedAmount = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            .format(item.amount)

        if (item.amount >= 0) {
            holder.tvAmount.setTextColor(Color.parseColor("#19AF59"))
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#D90429"))
        }
        holder.tvAmount.text = formattedAmount

        // DATE (agora correto)
        val ts = item.timestamp

        if (ts != null) {
            val date = ts.toDate()
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            holder.tvDate.text = formatter.format(date)

            Log.d("EXTRACT_ADAPTER", "Data formatada -> ${holder.tvDate.text}")
        } else {
            holder.tvDate.text = "--/--/----"
            Log.w("EXTRACT_ADAPTER", "Timestamp nulo para item ${item.id}")
        }
    }

    override fun getItemCount(): Int {
        Log.d("EXTRACT_ADAPTER", "getItemCount -> ${list.size}")
        return list.size
    }
}
