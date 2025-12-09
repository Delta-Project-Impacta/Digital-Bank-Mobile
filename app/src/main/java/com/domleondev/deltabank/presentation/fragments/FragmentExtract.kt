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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class FragmentExtract: Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private lateinit var tvBalance: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_extract, container, false)
    }

    // Dentro do seu onCreate ou onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvBalance = view.findViewById(R.id.textView7)   // Saldo

        // Puxa dados do Firebase
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserData(currentUser.uid)
        }

        // Olho para mostrar/ocultar saldo
        // Olho para mostrar/ocultar saldo
        val ivEye = view.findViewById<ImageView>(R.id.imageView5)
        var saldoVisivel = false
        tvBalance.text = "••••••" // saldo escondido por padrão

        ivEye.setOnClickListener {
            saldoVisivel = !saldoVisivel
            if (saldoVisivel) {
                // mostra saldo real
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    db.collection("users").document(currentUser.uid).get()
                        .addOnSuccessListener { doc ->
                            val balance = doc.getDouble("balance") ?: 0.0
                            val formattedBalance = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                                .format(balance)
                            tvBalance.text = formattedBalance
                        }
                }
            } else {
                // esconde saldo
                tvBalance.text = "•••••••"
            }
        }

        val btnOthers = view.findViewById<TextView>(R.id.extract_Extension_Period_Others)
        val containerCustomDates = view.findViewById<LinearLayout>(R.id.layout_Custom_Dates_Container)

        btnOthers.setOnClickListener {

            if (containerCustomDates.visibility == View.VISIBLE) {

                containerCustomDates.visibility = View.GONE

                btnOthers.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrowdown, 0)
            } else {

                containerCustomDates.visibility = View.VISIBLE

                btnOthers.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrowup, 0)
            }
        }
    }
    private fun fetchUserData(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // Saldo formatado
                    val balance = doc.getDouble("balance") ?: 0.0
                    val formattedBalance = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                        .format(balance)
                    tvBalance.text = formattedBalance
                }
            }
            .addOnFailureListener {
                Log.e("FragmentHome", "Erro ao puxar dados do usuário", it)
            }
    }
}