package com.domleondev.deltabank.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.activities.CardsActivity
import com.domleondev.deltabank.presentation.activities.PaymentHomeActivity
import com.domleondev.deltabank.presentation.activities.TransferHomeActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*

class FragmentHome : Fragment() {

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // TextViews dinâmicos
    private lateinit var tvName: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvInitials: TextView

    companion object {
        fun newInstance(): Fragment {
            val fragmentHome = FragmentHome()
            val args = Bundle()
            fragmentHome.arguments = args
            return fragmentHome
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TextViews do usuário
        tvName = view.findViewById(R.id.textView2)      // Nome
        tvBalance = view.findViewById(R.id.textView4)   // Saldo
        tvInitials = view.findViewById(R.id.textViewInitials) // Inicial

        // Puxa dados do Firebase
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserData(currentUser.uid)
        }

        // Olho para mostrar/ocultar saldo
        // Olho para mostrar/ocultar saldo
        val ivEye = view.findViewById<ImageView>(R.id.imageView)
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
                tvBalance.text = "••••••"
            }
        }

        // Cards clicáveis (precisa testar)
        val fragmentHomePixTransfer = view.findViewById<MaterialCardView>(R.id.fragment_Home_Pix_Transfer)
        fragmentHomePixTransfer.setOnClickListener {
            val intent = Intent(requireContext(), TransferHomeActivity::class.java)
            startActivity(intent)
        }

        val fragmentHomePayTransfer = view.findViewById<MaterialCardView>(R.id.fragment_Home_Pay_Transfer)
        fragmentHomePayTransfer.setOnClickListener {
            val intent = Intent(requireContext(), PaymentHomeActivity::class.java)
            startActivity(intent)
        }

        val fragmentHomeCards = view.findViewById<MaterialCardView>(R.id.fragment_Home_Cards)
        fragmentHomeCards.setOnClickListener {
            val intent = Intent(requireContext(), CardsActivity::class.java)
            startActivity(intent)
        }
    }

    // Função para puxar nome, saldo e iniciais do Firebase
    private fun fetchUserData(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val fullName = doc.getString("name") ?: "Usuário"
                    val firstName = fullName.split(" ").firstOrNull() ?: fullName

                    // Nome
                    tvName.text = firstName

                    // Iniciais
                    val initials = fullName.split(" ")
                        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                        .take(2)
                        .joinToString("")
                    tvInitials.text = initials

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
