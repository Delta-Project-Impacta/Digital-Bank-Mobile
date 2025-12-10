package com.domleondev.deltabank.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import android.widget.TextView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.domleondev.deltabank.presentation.dialogs.ExtractAdapter
import com.domleondev.deltabank.repository.request.TransactionRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager

class FragmentExtract: Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val transactionList = mutableListOf<TransactionRequest>()
    private lateinit var tvBalance: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("EXTRACT_FLOW", "onCreateView chamado — Inflando layout fragment_extract")
        return inflater.inflate(R.layout.fragment_extract, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("EXTRACT_FLOW", "onViewCreated chamado — Iniciando setup da tela")

        tvBalance = view.findViewById(R.id.textView7)
        recyclerView = view.findViewById(R.id.recyclerViewExtractList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val currentUser = auth.currentUser
        Log.d("EXTRACT_FLOW", "Usuário logado detectado? -> ${currentUser != null}")

        if (currentUser != null) {
            Log.d("EXTRACT_FLOW", "Chamando fetchUserData(${currentUser.uid})")
            fetchUserData(currentUser.uid)
        }

        if (currentUser != null) {
            Log.d("EXTRACT_FLOW", "Chamando loadTransactions(${currentUser.uid})")
            loadTransactions(currentUser.uid)
        }

        // Lógica do olho
        val ivEye = view.findViewById<ImageView>(R.id.imageView5)
        var saldoVisivel = false
        tvBalance.text = "R$ •••••••"

        ivEye.setOnClickListener {
            saldoVisivel = !saldoVisivel
            Log.d("EXTRACT_FLOW", "Clique no olho — saldoVisivel = $saldoVisivel")

            if (saldoVisivel) {
                val currentUser2 = auth.currentUser
                if (currentUser2 != null) {
                    db.collection("users").document(currentUser2.uid).get()
                        .addOnSuccessListener { doc ->
                            val balance = doc.getDouble("balance") ?: 0.0
                            val formattedBalance = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                                .format(balance)
                            Log.d("EXTRACT_FLOW", "Saldo revelado: $formattedBalance")
                            tvBalance.text = formattedBalance
                        }
                        .addOnFailureListener {
                            Log.e("EXTRACT_FLOW", "Erro ao buscar saldo ao clicar no olho", it)
                        }
                }
            } else {
                Log.d("EXTRACT_FLOW", "Saldo ocultado")
                tvBalance.text = "R$ ••••••••"
            }
        }

        val btnOthers = view.findViewById<TextView>(R.id.extract_Extension_Period_Others)
        val containerCustomDates = view.findViewById<LinearLayout>(R.id.layout_Custom_Dates_Container)

        btnOthers.setOnClickListener {

            Log.d("EXTRACT_FLOW", "Clique no botão 'Outros'")

            if (containerCustomDates.visibility == View.VISIBLE) {
                Log.d("EXTRACT_FLOW", "Escondendo filtros extras")
                containerCustomDates.visibility = View.GONE
                btnOthers.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrowdown, 0)
            } else {
                Log.d("EXTRACT_FLOW", "Mostrando filtros extras")
                containerCustomDates.visibility = View.VISIBLE
                btnOthers.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrowup, 0)
            }
        }
    }


    private fun fetchUserData(uid: String) {
        Log.d("EXTRACT_FLOW", "fetchUserData iniciado — uid=$uid")

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                Log.d("EXTRACT_FLOW", "fetchUserData — documento encontrado? ${doc.exists()}")

                if (doc.exists()) {
                    val balance = doc.getDouble("balance") ?: 0.0
                    val formattedBalance = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                        .format(balance)

                    Log.d("EXTRACT_FLOW", "Saldo carregado: $formattedBalance")
                    tvBalance.text = formattedBalance
                }
            }
            .addOnFailureListener {
                Log.e("EXTRACT_FLOW", "Erro ao carregar dados do usuário", it)
            }
    }


    private fun loadTransactions(uid: String) {

        Log.d("EXTRACT_FLOW", "loadTransactions iniciado — uid=$uid")

        db.collection("users")
            .document(uid)
            .collection("transactions")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->

                Log.d("EXTRACT_FLOW", "loadTransactions — resultado retornou ${result.size()} documentos")

                transactionList.clear()

                for (doc in result.documents) {
                    Log.d("EXTRACT_FLOW", "Processando doc: ${doc.id} -> ${doc.data}")

                    val t = doc.toObject(TransactionRequest::class.java)
                    if (t != null) {
                        transactionList.add(t.copy(id = doc.id))
                    } else {
                        Log.e("EXTRACT_FLOW", "Erro ao converter doc ${doc.id} para TransactionRequest")
                    }
                }

                Log.d("EXTRACT_FLOW", "Total de transações convertidas: ${transactionList.size}")

                recyclerView.adapter =
                    ExtractAdapter(transactionList.reversed())

                Log.d("EXTRACT_FLOW", "Adapter configurado com ${transactionList.size} transações")
            }
            .addOnFailureListener {
                Log.e("EXTRACT_FLOW", "Erro ao carregar transações", it)
            }
    }

}
