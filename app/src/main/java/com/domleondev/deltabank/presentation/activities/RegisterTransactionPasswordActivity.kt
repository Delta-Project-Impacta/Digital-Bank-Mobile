package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import com.domleondev.deltabank.R

import android.graphics.Color
import android.os.Build
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class RegisterTransactionPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterTransactionPasswordBinding
    private lateinit var passwordTransactionButtonBack: ImageView
    private val viewModel: RegisterTransactionPasswordViewModel by viewModels {
        RegisterTransactionPasswordViewModelFactory(RegisterTransactionPasswordUseCase(
            AuthRepository(FirebaseAuth.getInstance())))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterTransactionPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()

        passwordTransactionButtonBack = findViewById(R.id.password_Transaction_Button_Back)

        passwordTransactionButtonBack.setOnClickListener {
            finish()
        }

        //  Configuração universal de status bar transparente
        val window = window

// LÓGICA DE VERSÕES CORRIGIDA
        when {
            // Android 10 e anteriores (API < 30)
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // <--- ESSA É A CHAVE!

                @Suppress("DEPRECATION")
                window.statusBarColor = Color.TRANSPARENT
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.TRANSPARENT // <--- Força a cor aqui
            }

            // Android 11+ (API >= 30)
            else -> {
                // Este comando diz: "Não ajuste o layout pelas barras, deixe passar por trás"
                WindowCompat.setDecorFitsSystemWindows(window, false)

                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.isAppearanceLightStatusBars = true
                // controller.isAppearanceLightNavigationBars = true // Descomente se os ícones da navbar sumirem

                @Suppress("DEPRECATION")
                window.statusBarColor = Color.TRANSPARENT
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.TRANSPARENT // <--- Garante a transparência
            }
        }

        val headerContainer = findViewById<View>(R.id.password_Account_Toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.password_Account_Toolbar)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            headerContainer.setPadding(0, bars.top, 0, 0)
            insets
        }
    }


    private fun setupListeners() {

        // Botão "Next" (Mantive igual)
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


        val passwordField = binding.passwordEditAccount
        val confirmField = binding.passwordEditConfirmAccount

        // 1. GARANTIR QUE INICIA OCULTO E COM O ÍCONE DE OLHO (ic_eye)
        passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
        passwordField.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)

        confirmField.transformationMethod = PasswordTransformationMethod.getInstance()
        confirmField.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)

        // 2. LÓGICA DE CLIQUE PARA SENHA
        passwordField.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                // Verifica se clicou no ícone
                if (event.rawX >= (passwordField.right - passwordField.compoundDrawables[drawableEnd].bounds.width())) {

                    val selection = passwordField.selectionEnd // Salva posição do cursor
                    val isHidden = passwordField.transformationMethod is PasswordTransformationMethod

                    if (isHidden) {
                        // Estava oculto -> Torna VISÍVEL
                        passwordField.transformationMethod = null
                        // Muda ícone para "eye_off"
                        passwordField.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
                    } else {
                        // Estava visível -> Torna OCULTO
                        passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
                        // Muda ícone para "eye"
                        passwordField.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0)
                    }

                    passwordField.setSelection(selection) // Restaura cursor
                    return@setOnTouchListener true
                }
            }
            false
        }

        // 3. LÓGICA DE CLIQUE PARA CONFIRMAR SENHA (A mesma lógica acima)
        confirmField.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (confirmField.right - confirmField.compoundDrawables[drawableEnd].bounds.width())) {

                    val selection = confirmField.selectionEnd
                    val isHidden = confirmField.transformationMethod is PasswordTransformationMethod

                    if (isHidden) {
                        confirmField.transformationMethod = null
                        confirmField.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
                    } else {
                        confirmField.transformationMethod = PasswordTransformationMethod.getInstance()
                        confirmField.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0)
                    }

                    confirmField.setSelection(selection)
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