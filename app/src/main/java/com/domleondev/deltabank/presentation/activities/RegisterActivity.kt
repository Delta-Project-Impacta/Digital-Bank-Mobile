package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.domleondev.deltabank.viewModel.ValidationResult
import com.domleondev.deltabank.R
import com.domleondev.deltabank.databinding.ActivityRegisterBinding
import com.domleondev.deltabank.domain.usecase.ValidateBirthDateUseCase
import com.domleondev.deltabank.domain.usecase.ValidateCpfUseCase
import com.domleondev.deltabank.domain.usecase.ValidateEmailUseCase
import com.domleondev.deltabank.domain.usecase.ValidateNameUseCase
import com.domleondev.deltabank.domain.usecase.ValidatePhoneUseCase
import com.domleondev.deltabank.domain.usecase.addCpfMask
import com.domleondev.deltabank.domain.usecase.addDateMask
import com.domleondev.deltabank.domain.usecase.addPhoneMask
import kotlin.getValue
import com.domleondev.deltabank.viewModel.FirebaseCheckResult
import com.domleondev.deltabank.viewModel.RegisterViewModel
import com.domleondev.deltabank.viewModel.RegisterViewModelFactory

import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.domleondev.deltabank.databinding.RegisterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerTermBottomSheet : TextView
    private lateinit var checkBox: CheckBox

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

        registerTermBottomSheet = findViewById(R.id.register_Term_Bottom_Sheet)
        checkBox = findViewById(R.id.check_Box)

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
        val headerContainer = findViewById<View>(R.id.register_Toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            headerContainer.setPadding(0, bars.top, 0, 0)
            insets
        }

        binding.registerTermBottomSheet.setOnClickListener {
            showBottomSheetDialog()
        }
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
            val isChecked = checkBox.isChecked

            viewModel.validateFields(name, cpf, birth, email, confirmEmail, phone, isChecked)
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
                        "invalid_term" -> R.string.error_invalid_term
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
    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        val sheetBinding: RegisterBottomSheetBinding =
            RegisterBottomSheetBinding.inflate(layoutInflater, null, false)

        dialog.setContentView(sheetBinding.root)

        sheetBinding.registerBottomClose.setOnClickListener {
            dialog.dismiss()
        }
        sheetBinding.registerTermButton.setOnClickListener {
            checkBox.isChecked = true
            dialog.dismiss()
        }

        // Permite fechar tocando fora (opcional)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.show()
    }
}
