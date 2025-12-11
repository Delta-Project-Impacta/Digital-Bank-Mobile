package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.domleondev.deltabank.R
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.domleondev.deltabank.databinding.ActivityRegisterLoginPasswordBinding
import com.domleondev.deltabank.domain.usecase.RegisterLoginPasswordUseCase
import com.domleondev.deltabank.viewModel.RegisterLoginPasswordViewModelFactory
import com.domleondev.deltabank.presentation.states.RegisterLoginPasswordState
import com.domleondev.deltabank.viewModel.RegisterLoginPasswordViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterLoginPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterLoginPasswordBinding

    private val viewModel: RegisterLoginPasswordViewModel by viewModels {
        RegisterLoginPasswordViewModelFactory(
            RegisterLoginPasswordUseCase()
        )
    }

    private var debounceJob: Job? = null

    private lateinit var email: String
    private lateinit var name: String
    private lateinit var cpf: String
    private lateinit var birth: String
    private lateinit var loginPassword: String

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_NOME = "extra_name"
        const val EXTRA_CPF = "extra_cpf"
        const val EXTRA_BIRTH = "extra_birth"
        const val EXTRA_LOGIN_PASSWORD = "extra_login_password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterLoginPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.passwordButtonBack.setOnClickListener { finish() }

        setupFields()
        observeViewModel()

        limitToSix(binding.passwordEdit)
        limitToSix(binding.passwordEditConfirm)
    }

    private fun setupFields() {
        email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()
        name = intent.getStringExtra(EXTRA_NOME).orEmpty()
        cpf = intent.getStringExtra(EXTRA_CPF).orEmpty()
        birth = intent.getStringExtra(EXTRA_BIRTH).orEmpty()
        loginPassword = intent.getStringExtra(EXTRA_LOGIN_PASSWORD).orEmpty()


        binding.passwordButtonNext.setOnClickListener {

            loginPassword = binding.passwordEdit.text.toString()
            val confirmLoginPassword = binding.passwordEditConfirm.text.toString()

            debounceJob?.cancel()
            debounceJob = lifecycleScope.launch {
                delay(250)

                Log.d("RegisterActivity", "ButtonNext clicked - launching register in ViewModel")
                Log.d("RegisterActivity", "Input values -> email: $email, name: $name, cpf: $cpf, birth: $birth, loginPassword: ${this@RegisterLoginPasswordActivity.loginPassword}")
                Log.d("RegisterActivity", "ButtonNext clicked. loginPassword now: $loginPassword")

                viewModel.register(
                    email = email,
                    loginPassword = loginPassword,
                    confirmPassword = confirmLoginPassword,
                    name = name,
                    birth = birth
                )
            }
        }

        setupPasswordToggle(binding.passwordEdit)
        setupPasswordToggle(binding.passwordEditConfirm)

        updateSaveButton()
    }

    private fun setupPasswordToggle(edit: TextInputEditText) {

        // Começa com a senha escondida e com olho fechado
        edit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        edit.setSelection(edit.text?.length ?: 0)
        edit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0)

        edit.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {

                val drawableEnd = edit.compoundDrawables[2] ?: return@setOnTouchListener false

                if (event.rawX >= (edit.right - drawableEnd.bounds.width())) {

                    val isPasswordHidden =
                        edit.inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD ==
                                InputType.TYPE_NUMBER_VARIATION_PASSWORD

                    if (isPasswordHidden) {
                        // Mostrar senha
                        edit.inputType = InputType.TYPE_CLASS_NUMBER
                        edit.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.drawable.ic_eye, 0
                        )
                    } else {
                        // Ocultar senha
                        edit.inputType = InputType.TYPE_CLASS_NUMBER or
                                InputType.TYPE_NUMBER_VARIATION_PASSWORD
                        edit.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.drawable.ic_eye_off, 0
                        )
                    }

                    edit.setSelection(edit.text?.length ?: 0)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun limitToSix(edit: TextInputEditText) {
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 6) {
                    Toast.makeText(
                        this@RegisterLoginPasswordActivity,
                        "A senha deve ter só 6 números",
                        Toast.LENGTH_SHORT
                    ).show()
                    edit.setText(s.substring(0, 6))
                    edit.setSelection(6)
                }
                updateSaveButton()
            }
        })
    }

    private fun updateSaveButton() {
        binding.passwordButtonNext.isEnabled =
            binding.passwordEdit.text?.isNotEmpty() == true &&
                    binding.passwordEditConfirm.text?.isNotEmpty() == true
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                Log.d("RegisterActivity", "Collecting state: $state")
                when (state) {

                    RegisterLoginPasswordState.Idle -> {
                        Log.d("RegisterActivity", "State: Idle")
                        binding.progressBar.visibility = android.view.View.GONE
                        binding.passwordButtonNext.isEnabled = true
                    }

                    RegisterLoginPasswordState.Loading -> {
                        Log.d("RegisterActivity", "State: Loading")
                        binding.progressBar.visibility = android.view.View.VISIBLE
                        binding.passwordButtonNext.isEnabled = false
                    }

                    is RegisterLoginPasswordState.Success -> {
                        Log.d("RegisterActivity", "State: Success")
                        binding.progressBar.visibility = android.view.View.GONE

                        Toast.makeText(
                            this@RegisterLoginPasswordActivity,
                            "Senha cadastrada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val next = Intent(
                            this@RegisterLoginPasswordActivity,
                            RegisterTransactionPasswordActivity::class.java
                        )

                        next.putExtra(EXTRA_EMAIL, email)
                        next.putExtra(EXTRA_NOME, name)
                        next.putExtra(EXTRA_CPF, cpf)
                        next.putExtra(EXTRA_BIRTH, birth)
                        next.putExtra(EXTRA_LOGIN_PASSWORD, loginPassword
                        )


                        Log.d("RegisterActivity", "Preparing Intent for next Activity")
                        Log.d("RegisterActivity", "Extras: email=$email, name=$name, cpf=$cpf, birth=$birth, loginPassword=$loginPassword")
                        startActivity(next)
                        Log.d("RegisterActivity", "Intent fired -> moving to RegisterTransactionPasswordActivity")
                        finish()
                    }

                    is RegisterLoginPasswordState.Error -> {
                        Log.d("RegisterActivity", "State: Error - ${state.message}")
                        binding.progressBar.visibility = android.view.View.GONE
                        binding.passwordButtonNext.isEnabled = true

                        Toast.makeText(
                            this@RegisterLoginPasswordActivity,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d("RegisterActivity", "Resetting ViewModel state after error")
                        viewModel.resetState() // resetar o status para mostrar toast novo, TESTAR

                    }
                }
            }
        }
    }
}
