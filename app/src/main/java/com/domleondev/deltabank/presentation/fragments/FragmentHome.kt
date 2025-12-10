package com.domleondev.deltabank.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.activities.CardsActivity
import com.domleondev.deltabank.presentation.activities.LoginActivity
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
        tvName = view.findViewById(R.id.textView2)
        tvBalance = view.findViewById(R.id.textView4)
        tvInitials = view.findViewById(R.id.textViewInitials)

        // Puxa dados do Firebase
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserData(currentUser.uid)
        } else {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Olho para mostrar/ocultar saldo
        val ivEye = view.findViewById<ImageView>(R.id.imageView)
        var saldoVisivel = false
        tvBalance.text = "••••••••"

        ivEye.setOnClickListener {
            saldoVisivel = !saldoVisivel
            if (saldoVisivel) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    db.collection("users").document(currentUser.uid).get()
                        .addOnSuccessListener { doc ->
                            val balance = doc.getDouble("balance") ?: 0.0
                            val formattedBalance =
                                NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                                    .format(balance)
                            tvBalance.text = formattedBalance
                        }
                }
            } else {
                tvBalance.text = "••••••••"
            }
        }

        // Cards clicáveis
        val fragmentHomePixTransfer =
            view.findViewById<MaterialCardView>(R.id.fragment_Home_Pix_Transfer)
        fragmentHomePixTransfer.setOnClickListener {
            val intent = Intent(requireContext(), TransferHomeActivity::class.java)
            startActivity(intent)
        }

        val fragmentHomePayTransfer =
            view.findViewById<MaterialCardView>(R.id.fragment_Home_Pay_Transfer)
        fragmentHomePayTransfer.setOnClickListener {
            val intent = Intent(requireContext(), PaymentHomeActivity::class.java)
            startActivity(intent)
        }

        val fragmentHomeCards = view.findViewById<MaterialCardView>(R.id.fragment_Home_Cards)
        fragmentHomeCards.setOnClickListener {
            val intent = Intent(requireContext(), CardsActivity::class.java)
            startActivity(intent)
        }

        // --- CORREÇÃO DA TRANSPARÊNCIA (NOVIDADE AQUI) ---
        // Pegamos a referência da Toolbar
        val toolbar = view.findViewById<View>(R.id.fragment_Home_Toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Aplicamos padding APENAS na Toolbar.
            // O view principal (fundo laranja) continua esticado lá no topo (atrás do relógio).
            // A Toolbar desce a altura da barra de status (bars.top) para o texto aparecer.
            toolbar.setPadding(
                toolbar.paddingLeft,
                bars.top, // AQUI: Empurra o título para baixo
                toolbar.paddingRight,
                toolbar.paddingBottom
            )

            // Se o final da sua tela estiver cortando atrás da BottomNav,
            // você pode adicionar um padding bottom no view principal aqui também:
            // v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bars.bottom)

            insets
        }
    }

    private fun fetchUserData(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val fullName = doc.getString("name") ?: "Usuário"
                    val firstName = fullName.split(" ").firstOrNull() ?: fullName

                    tvName.text = firstName

                    val initials = fullName.split(" ")
                        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                        .take(2)
                        .joinToString("")
                    tvInitials.text = initials

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