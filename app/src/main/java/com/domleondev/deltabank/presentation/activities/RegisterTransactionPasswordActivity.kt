package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.domleondev.deltabank.databinding.ActivityRegisterTransactionPasswordBinding
import com.domleondev.deltabank.repository.auth.AuthRepository
import com.domleondev.deltabank.presentation.states.RegisterTransactionPasswordState
import com.domleondev.deltabank.domain.usecase.RegisterTransactionPasswordUseCase
import com.domleondev.deltabank.viewModel.RegisterTransactionPasswordViewModel
import com.domleondev.deltabank.viewModel.RegisterTransactionPasswordViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegisterTransactionPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterTransactionPasswordBinding
    private val viewModel: RegisterTransactionPasswordViewModel by viewModels {
        RegisterTransactionPasswordViewModelFactory(RegisterTransactionPasswordUseCase(
            AuthRepository(FirebaseAuth.getInstance())))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterTransactionPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {

        // Botão "Next"
        binding.passwordAccountButtonNext.setOnClickListener {
            val transactionPassword = binding.passwordEditAccount.text.toString()
            val confirmTransactionPassword = binding.passwordEditConfirmAccount.text.toString()

            val email = intent.getStringExtra(RegisterLoginPasswordActivity.EXTRA_EMAIL).orEmpty()
            val name = intent.getStringExtra(RegisterLoginPasswordActivity.EXTRA_NOME).orEmpty()
            val cpf = intent.getStringExtra(RegisterLoginPasswordActivity.EXTRA_CPF).orEmpty()
            val birth = intent.getStringExtra(RegisterLoginPasswordActivity.EXTRA_BIRTH).orEmpty()
            val loginPassword = intent.getStringExtra(RegisterLoginPasswordActivity.EXTRA_LOGIN_PASSWORD).orEmpty()

            viewModel.register(
                email = email,
                loginPassword = loginPassword,
                name = name,
                cpf = cpf,
                birthDate = birth,
                transactionPassword = transactionPassword,
                confirmTransactionPassword = confirmTransactionPassword
            )
            Log.d("RegisterActivity", "Botão Next clicado. Capturando dados do formulário")
            Log.d("RegisterActivity", "Email: $email, Nome: $name, CPF: $cpf, Birth: $birth, LoginPassword: $loginPassword, TransactionPassword: $transactionPassword")
        }


        // ----- Olho senha -----
        val passwordField = binding.passwordEditAccount
        passwordField.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (passwordField.right - passwordField.compoundDrawables[drawableEnd].bounds.width())) {
                    val isHidden = passwordField.transformationMethod is PasswordTransformationMethod
                    passwordField.transformationMethod =
                        if (isHidden) null else PasswordTransformationMethod.getInstance()
                    passwordField.setSelection(passwordField.text?.length ?: 0)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // ----- Olho confirmar senha -----
        val confirmField = binding.passwordEditConfirmAccount
        confirmField.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (confirmField.right - confirmField.compoundDrawables[drawableEnd].bounds.width())) {
                    val isHidden = confirmField.transformationMethod is PasswordTransformationMethod
                    confirmField.transformationMethod =
                        if (isHidden) null else PasswordTransformationMethod.getInstance()
                    confirmField.setSelection(confirmField.text?.length ?: 0)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                Log.d("RegisterActivity", "ViewModel state changed: $state")
                when (state) {

                    is RegisterTransactionPasswordState.Idle -> {
                        Log.d("RegisterActivity", "State: Idle")
                        binding.passwordAccountProgress.visibility = View.GONE
                        binding.passwordAccountButtonNext.isEnabled = true
                    }

                    is RegisterTransactionPasswordState.Loading -> {
                        Log.d("RegisterActivity", "State: Loading")
                        binding.passwordAccountProgress.visibility = View.VISIBLE
                        binding.passwordAccountButtonNext.isEnabled = false
                    }

                    is RegisterTransactionPasswordState.Success -> {
                        Log.d("RegisterActivity", "State: Success -> navigating to next activity")
                        binding.passwordAccountProgress.visibility = View.GONE

                        Toast.makeText(
                            this@RegisterTransactionPasswordActivity,
                            "Conta criada com sucesso. Faça seu login!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Atualiza displayName do FirebaseAuth igual ao outro app
                        val name = intent.getStringExtra(RegisterLoginPasswordActivity.EXTRA_NOME)
                        val user = FirebaseAuth.getInstance().currentUser
                        val profileUpdates = com.google.firebase.auth.ktx.userProfileChangeRequest {
                            displayName = name
                        }

                        // Vai para próxima activity
                        startActivity(Intent(this@RegisterTransactionPasswordActivity, LoginActivity::class.java))
                        finish()
                    }

                    is RegisterTransactionPasswordState.Error -> {
                        Log.d("RegisterActivity", "State: Error -> ${state.message}")
                        binding.passwordAccountProgress.visibility = View.GONE
                        binding.passwordAccountButtonNext.isEnabled = true

                        val msg = state.message
                        val toShow = when {
                            msg.contains("already in use", ignoreCase = true) -> "E-mail já está em uso"
                            msg.contains("As senhas não conferem", ignoreCase = true) -> "As senhas não conferem"
                            msg.contains("Senha fraca", ignoreCase = true) -> "Senha fraca"
                            else -> "Erro inesperado"
                        }

                        Toast.makeText(this@RegisterTransactionPasswordActivity, toShow, Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                }
            }
        }
    }
}