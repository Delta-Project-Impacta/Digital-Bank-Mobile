package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.domleondev.deltabank.R
import com.domleondev.deltabank.domain.usecase.LoginUseCase
import com.domleondev.deltabank.domain.usecase.addCpfMask
import com.domleondev.deltabank.repository.auth.AuthRepository
import com.domleondev.deltabank.viewModel.LoginViewModel
import com.domleondev.deltabank.viewModel.LoginViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) auth.signOut()

        val repository = AuthRepository(auth)
        val loginUseCase = LoginUseCase(repository)
        val factory = LoginViewModelFactory(loginUseCase, repository)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        val cpfInput = findViewById<TextInputEditText>(R.id.input_cpf)
        val passwordInput = findViewById<TextInputEditText>(R.id.input_password)
        val loginButton = findViewById<AppCompatButton>(R.id.btn_login)
        cpfInput.addCpfMask()

        suspend fun validateCpfInFirebase(cpfClean: String): Boolean {
            return viewModel.checkCpfExists(cpfClean)
        }

        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(this@LoginActivity, "Biometria verificada!", Toast.LENGTH_SHORT).show()

                    val sharedPref = getSharedPreferences("login_data", MODE_PRIVATE)

                    val cpfText = if (cpfInput.text.toString().isNotEmpty())
                        cpfInput.text.toString()
                    else
                        sharedPref.getString("last_cpf", "") ?: ""

                    val cpfClean = cpfText.replace(Regex("[^\\d]"), "")

                    // Pega a senha salva para o CPF
                    val savedPassword = sharedPref.getString("${cpfClean}_password", "") ?: ""
                    val firstTimeDone = sharedPref.getBoolean("${cpfClean}_first_time_done", false)

                    val passwordText = if (passwordInput.text.toString().isNotEmpty())
                        passwordInput.text.toString()
                    else
                        savedPassword

                    if (cpfClean.isNotEmpty() && passwordText.isNotEmpty() && firstTimeDone) {
                        lifecycleScope.launch {
                            val exists = viewModel.checkCpfExists(cpfClean)
                            if (exists) {
                                viewModel.login(cpfClean, passwordText)
                            } else {
                                Toast.makeText(this@LoginActivity, "CPF não encontrado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Preencha a senha pela primeira vez para usar a biometria",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        Toast.makeText(this@LoginActivity, "Biometria cancelada", Toast.LENGTH_SHORT).show()
                        return
                    }
                    Toast.makeText(this@LoginActivity, "Erro: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@LoginActivity, "Biometria não reconhecida", Toast.LENGTH_SHORT).show()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirme sua identidade")
            .setSubtitle("Use sua biometria para continuar")
            .setNegativeButtonText("Cancelar")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) biometricPrompt.authenticate(promptInfo)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginResult.collect { result ->
                result ?: return@collect
                if (result.isSuccess) {
                    val sharedPref = getSharedPreferences("login_data", MODE_PRIVATE)
                    val cpfClean = cpfInput.text.toString().replace(Regex("[^\\d]"), "")
                    sharedPref.edit().apply {
                        putString("${cpfClean}_password", passwordInput.text.toString())
                        putBoolean("${cpfClean}_first_time_done", true) // Marca que digitou senha
                        putString("last_cpf", cpfClean)
                        apply()
                    }
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "CPF ou senha incorretos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginButton.setOnClickListener {
            val cpfText = cpfInput.text.toString()
            val passwordText = passwordInput.text.toString()
            val cpfClean = cpfText.replace(Regex("[^\\d]"), "")

            if (cpfClean.length != 11) {
                Toast.makeText(this, "Digite os 11 dígitos do CPF", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passwordText.length < 6) {
                Toast.makeText(this, "A senha deve conter no mínimo 6 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val exists = validateCpfInFirebase(cpfClean)
                if (!exists) {
                    Toast.makeText(this@LoginActivity, "CPF não encontrado", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                viewModel.login(cpfClean, passwordText)
            }
        }

        findViewById<TextView>(R.id.tv_forgot_password).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        findViewById<ImageView>(R.id.back_arrow).setOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}
