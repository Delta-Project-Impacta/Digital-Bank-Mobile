package com.domleondev.deltabank.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.widget.Toast
import com.domleondev.deltabank.R
import com.domleondev.deltabank.databinding.FragmentProfileBinding
import com.domleondev.deltabank.presentation.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentProfile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid

            // Puxando nome completo do Firestore
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                val fullName = doc.getString("name") ?: "Usuário"
                val cpf = doc.getString("cpf") ?: "000.000.000-00"

                // Nome completo
                binding.profileName.text = fullName

                // Iniciais
                binding.profileLabelInitials.text = getInitials(fullName)

                // Pix
                binding.profilePixValue.text = cpf

                // Compartilhar conta
                binding.profileOptionsContainer.getChildAt(0).setOnClickListener {
                    shareAccount(
                        name = fullName,
                        agency = binding.profileAgencyNumber.text.toString(),
                        account = binding.profileAccountNumber.text.toString(),
                        pix = cpf,
                        bank = binding.profileBankNumber.text.toString()
                    )
                }
            }
        } else {

            // Usuário não logado → redireciona pro login - FUNCIONOUUUUUUUUUUU AMÉNNNNNNNNN
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        // Logout
        binding.profileButtonLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logout realizado", Toast.LENGTH_SHORT).show()
            // Redireciona para LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Back
        binding.fragmentProfileButtonBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.content, FragmentHome())
                .commit()
        }

    }
    private fun getInitials(fullName: String): String {
        return fullName.split(" ").map { it.firstOrNull()?.uppercaseChar() ?: ' ' }.joinToString("")
            .take(2)
    }

    private fun shareAccount(name: String, agency: String, account: String, pix: String, bank: String) {
        val shareText = """
            Banco: $bank
            Agência: $agency
            Conta: $account
            Titular: $name
            Pix: $pix
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Compartilhar Conta"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
