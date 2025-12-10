package com.domleondev.deltabank.presentation.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.domleondev.deltabank.repository.tranfersrepository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore


class DevPopulateTransactionsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val transactionRepo = TransactionRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        populateAllUsers()
    }

    private fun populateAllUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val uid = doc.id

                    // Adiciona 3 transações de exemplo para cada usuário
                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "deposit",
                        amount = 5745.0,
                        description = "Depósito inicial",
                        onSuccess = { println("OK 1") },
                        onFailure = { println("ERRO 1") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "card_purchase",
                        amount = -45.90,
                        description = "Compra no Débito",
                        onSuccess = { println("OK 2") },
                        onFailure = { println("ERRO 2") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "card_purchase",
                        amount = -57.90,
                        description = "Compra no Débito",
                        onSuccess = { println("OK 3") },
                        onFailure = { println("ERRO 3") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "card_purchase",
                        amount = -69.90,
                        description = "Compra no Débito",
                        onSuccess = { println("OK 4") },
                        onFailure = { println("ERRO 4") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "card_purchase",
                        amount = -475.90,
                        description = "Compra no Débito",
                        onSuccess = { println("OK 5") },
                        onFailure = { println("ERRO 5") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "card_purchase",
                        amount = -22.90,
                        description = "Compra no Débito",
                        onSuccess = { println("OK 6") },
                        onFailure = { println("ERRO 6") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "card_purchase",
                        amount = -199.90,
                        description = "Compra no Débito",
                        onSuccess = { println("OK 7") },
                        onFailure = { println("ERRO 7") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "card_purchase",
                        amount = -342.50,
                        description = "Compra no Débito",
                        onSuccess = { println("OK 8") },
                        onFailure = { println("ERRO 8") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "card_purchase",
                        amount = 5999.90,
                        description = "Compra no Crédito",
                        onSuccess = { println("OK 9") },
                        onFailure = { println("ERRO 9") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "card_purchase",
                        amount = 3200.50,
                        description = "Compra no Crédito",
                        onSuccess = { println("OK 10") },
                        onFailure = { println("ERRO 10") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "pix_received",
                        amount = 80.0,
                        description = "PIX recebido de Susana",
                        onSuccess = { println("OK 11") },
                        onFailure = { println("ERRO 11") }
                    )
                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "pix_received",
                        amount = 900.0,
                        description = "PIX recebido de Alexandra",
                        onSuccess = { println("OK 12") },
                        onFailure = { println("ERRO 12") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "pix_received",
                        amount = 9600.0,
                        description = "PIX recebido de Cristiano",
                        onSuccess = { println("OK 13") },
                        onFailure = { println("ERRO 13") }
                    )

                    transactionRepo.addTransaction(
                        userId = uid,
                        type = "pix_received",
                        amount = 800.0,
                        description = "PIX recebido de Rafael",
                        onSuccess = { println("OK 14") },
                        onFailure = { println("ERRO 14") }
                    )
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao buscar usuários: $e")
            }
    }
}
