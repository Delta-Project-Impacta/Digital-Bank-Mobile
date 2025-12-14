package com.domleondev.deltabank.domain.usecase

import android.util.Log
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Receipt(
    val title: String,
    val bodyText: String,
    val formattedAmount: String,
    val formattedDateTime: String
)

class PrepareReceiptUseCase {

    private val TAG = "PrepareReceiptUseCase"
    private val moneyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    fun prepare(
        originName: String?,
        originCpf: String?,
        destinyName: String?,
        destinyCpf: String?,
        amount: Double,
        fromTransactionId: String?,
        toTransactionId: String?,
        newFromBalance: Double?,
        newToBalance: Double?
    ): Receipt {
        Log.d(TAG, "prepare -> origin=$originName destiny=$destinyName amount=$amount fromTx=$fromTransactionId toTx=$toTransactionId")

        val formattedAmount = moneyFormat.format(amount)
        val now = Date()
        val formattedDateTime = dateTimeFormat.format(now)

        val sb = StringBuilder()
        sb.append("Comprovante de Transferência\n")
        sb.append("Banco: DeltaBank\n")
        sb.append("Tipo: PIX\n")
        sb.append("Data: $formattedDateTime\n")
        sb.append("Valor: $formattedAmount\n")
        sb.append("\nDestinatário:\n")
        sb.append("Nome: ${destinyName ?: "--"}\n")
        sb.append("CPF: ${destinyCpf ?: "--"}\n")
        sb.append("\nRemetente:\n")
        sb.append("Nome: ${originName ?: "Você"}\n")
        sb.append("CPF: ${originCpf ?: "--"}\n")
        sb.append("\nIDs das transações:\n")
        sb.append("Remetente: ${fromTransactionId ?: "--"}\n")
        sb.append("Destinatário: ${toTransactionId ?: "--"}\n")
        sb.append("\nSaldo após transação:\n")
        sb.append("Seu saldo: ${if (newFromBalance != null) moneyFormat.format(newFromBalance) else "--"}\n")
        sb.append("Saldo do destinatário: ${if (newToBalance != null) moneyFormat.format(newToBalance) else "--"}\n")
        sb.append("\nComprovante gerado pelo app DeltaBank.")

        val receipt = Receipt(
            title = "Comprovante - DeltaBank",
            bodyText = sb.toString(),
            formattedAmount = formattedAmount,
            formattedDateTime = formattedDateTime
        )

        Log.d(TAG, "Receipt preparado -> amount=$formattedAmount date=$formattedDateTime")
        return receipt
    }
}
