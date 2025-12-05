package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.domleondev.deltabank.presentation.viewModel.ValidationResult
import com.domleondev.deltabank.R
import com.domleondev.deltabank.databinding.ActivityRegisterBinding
import com.domleondev.deltabank.presentation.addCpfMask
import com.domleondev.deltabank.presentation.addDateMask
import com.domleondev.deltabank.presentation.addPhoneMask
import kotlin.getValue
import com.domleondev.deltabank.presentation.usecase.*
import com.domleondev.deltabank.presentation.viewModel.FirebaseCheckResult
import com.domleondev.deltabank.presentation.viewModel.RegisterViewModel
import com.domleondev.deltabank.presentation.viewModel.RegisterViewModelFactory
import com.google.firebase.auth.FirebaseAuth



class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(
            ValidateNameUseCase(),
            ValidateBirthDateUseCase(),
            ValidateCpfUseCase(),
            ValidateEmailUseCase(),
            ValidatePhoneUseCase()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyMasks()
        setupListeners()
        observeValidation()
        observeFirebaseCheck()
    }

    private fun applyMasks() {
        binding.registerEditCpf.addCpfMask()
        binding.registerEditBirth.addDateMask()
        binding.registerEditPhone.addPhoneMask()
    }

    private fun setupListeners() {
        binding.registerButtonNext.setOnClickListener {
            val name = binding.registerEditNome.text.toString()
            val cpf = binding.registerEditCpf.text.toString()
            val birth = binding.registerEditBirth.text.toString()
            val email = binding.registerEditEmail.text.toString()
            val confirmEmail = binding.registerEditConfirmEmail.text.toString()
            val phone = binding.registerEditPhone.text.toString()

            viewModel.validateFields(name, cpf, birth, email, confirmEmail, phone)
        }

        binding.registerButtonBack.setOnClickListener { finish() }
    }

    private fun observeValidation() {
        viewModel.validationState.observe(this) { result ->
            when (result) {

                is ValidationResult.Success -> {
                    val cpf = binding.registerEditCpf.text.toString()
                    val email = binding.registerEditEmail.text.toString()

                    viewModel.checkCpfAndEmailInFirebase(cpf, email)
                }

                is ValidationResult.Error -> {

                    val message = when (result.errorKey) {
                        "invalid_name" -> R.string.error_invalid_name
                        "invalid_cpf" -> R.string.error_invalid_cpf
                        "invalid_birth" -> R.string.error_invalid_birth
                        "invalid_email" -> R.string.error_invalid_email
                        "email_not_match" -> R.string.error_email_not_match
                        "invalid_phone" -> R.string.error_invalid_phone
                        else -> R.string.error_generic
                    }
                    Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeFirebaseCheck() {
        viewModel.firebaseCheckState.observe(this) { state ->
            when (state) {

                FirebaseCheckResult.CpfExists -> {
                    Toast.makeText(this, getString(R.string.error_cpf_exists), Toast.LENGTH_SHORT).show()
                }

                FirebaseCheckResult.EmailExists -> {
                    Toast.makeText(this, getString(R.string.error_email_exists), Toast.LENGTH_SHORT).show()
                }

                FirebaseCheckResult.Error -> {
                    Toast.makeText(this, getString(R.string.error_firebase_generic), Toast.LENGTH_SHORT).show()
                }

                FirebaseCheckResult.Ok -> navigateToAddress()
            }
        }
    }

    private fun navigateToAddress() {
        val intent = Intent(this, RegisterAddressActivity::class.java).apply {
            putExtra("name", binding.registerEditNome.text.toString())
            putExtra("cpf", binding.registerEditCpf.text.toString())
            putExtra("birth", binding.registerEditBirth.text.toString())
            putExtra("email", binding.registerEditEmail.text.toString())
            putExtra("phone", binding.registerEditPhone.text.toString())
        }

        startActivity(intent)
    }
}
