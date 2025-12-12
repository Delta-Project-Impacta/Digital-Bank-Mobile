package com.domleondev.deltatransfers.presentation.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.domleondev.deltabank.R
import com.domleondev.deltabank.databinding.ActivityTransfersDataBinding
import com.domleondev.deltabank.databinding.CustomBottomSheetBinding
import com.domleondev.deltabank.domain.usecase.applyAccountMask
import com.domleondev.deltabank.presentation.activities.TransferAmountActivity
import com.domleondev.deltabank.viewModel.TransfersViewModel
import com.domleondev.deltabank.viewModel.TransfersViewModel.ValidationError
import com.domleondev.deltabank.viewModel.TransfersViewModel.BankItem
import com.example.freela.presentation.adapter.BottomSheetTransfersAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.collections.listOf
import kotlin.text.matches

class TransfersDataActivity : AppCompatActivity() {

    private val viewModel: TransfersViewModel by viewModels ()
    private lateinit var layoutBankName : TextInputLayout
    private lateinit var layoutAgency : TextInputLayout
    private lateinit var layoutAccount : TextInputLayout
    private lateinit var layoutAccountType : TextInputLayout

    private lateinit var transfersInputEditListBank : TextView
    private lateinit var transfersInputAgency : TextInputEditText
    private lateinit var transfersInputAccount: TextInputEditText
    private lateinit var transfersInputTypeAccount : TextView
    private lateinit var transfersButtonNext : AppCompatButton
    private lateinit var binding: ActivityTransfersDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
        binding = ActivityTransfersDataBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val transfersButtonBack = findViewById<ImageView>(R.id.payment_Transfers_Arrow_Back)
        transfersInputEditListBank = findViewById(R.id.transfers_Edit_Name_Bank)
        transfersInputAgency = findViewById(R.id.transfers_Edit_Agency)
        transfersInputAccount = findViewById(R.id.transfers_Edit_Account)
        transfersInputTypeAccount = findViewById(R.id.dropdown_Account_Type)
        transfersButtonNext = findViewById(R.id.payment_Transfers_Button_Next)

        layoutBankName = findViewById(R.id.transfers_Input_Name_Bank)
        layoutAgency = findViewById(R.id.transfers_Input_Agency)
        layoutAccount = findViewById(R.id.transfers_Input_Account)
        layoutAccountType = findViewById(R.id.transfers_Input_Account_Type)

        binding.transfersEditAccount.applyAccountMask()

        transfersButtonBack.setOnClickListener {
            finish()
        }

        val transfersAccountType = listOf(
            getString(R.string.transfers_account_type_current),
            getString(R.string.transfers_bank_account_type_savings)
        )

        val adapter = ArrayAdapter(this, R.layout.item_dropdown_option, transfersAccountType)

        binding.dropdownAccountType.setAdapter(adapter)

        transfersButtonNext.setOnClickListener {

            viewModel.validateFields(
                transfersInputEditListBank.text.toString(),
                transfersInputAgency.text.toString(),
                transfersInputAccount.text.toString(),
                transfersInputTypeAccount.text.toString()
            )
        }

        binding.transfersEditNameBank.setOnClickListener {
            viewModel.seekBanks()
        }
        viewModel.banks.observe(this) { lista ->
            showButtonSheetDialog(lista)
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
        val headerContainer = findViewById<View>(R.id.transfer_Data)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            headerContainer.setPadding(0, bars.top, 0, 0)
            insets
        }
    }

        private fun setupObservers() {
            // 1. Observa a lista de erros
            viewModel.errorsList.observe(this) { errorsList ->
                if (errorsList.isNotEmpty()) {
                    showFieldErrors(errorsList)
                }
            }
            viewModel.isButtonEnabled.observe(this) { enabled ->
                transfersButtonNext.isEnabled = enabled
                transfersButtonNext.alpha = if (enabled) 1f else 0.5f // visual feedback
            }

            viewModel.allValid.observe(this) { valid ->
                if (valid) {
                    val intent = Intent(this, TransferAmountActivity::class.java)
                    startActivity(intent)
                } else {
                    // Remove erros em tempo real
                    transfersInputEditListBank.doOnTextChanged { text, _, _, _ ->
                        if (!text.isNullOrBlank()) layoutBankName.error = null
                        checkFieldsToEnableButton()
                    }

                    transfersInputAgency.doOnTextChanged { text, _, _, _ ->
                        if (!text.isNullOrBlank()) layoutAgency.error = null
                        checkFieldsToEnableButton()
                    }

                    transfersInputAccount.doOnTextChanged { text, _, _, _ ->
                        if (!text.isNullOrBlank() &&
                            text.matches(Regex("^\\d{1,8}(-[\\dX])?$"))
                        ) layoutAccount.error = null
                        checkFieldsToEnableButton()
                    }

                    transfersInputTypeAccount.doOnTextChanged { text, _, _, _ ->
                        if (!text.isNullOrBlank()) layoutAccountType.error = null
                        checkFieldsToEnableButton()
                    }
                }
            }
        }

    private fun checkFieldsToEnableButton() {
        val hasError = listOf(
            layoutBankName.error,
            layoutAgency.error,
            layoutAccount.error,
            layoutAccountType.error
        ).any { it != null }

        val allFilled = listOf(
            transfersInputEditListBank.text,
            transfersInputAgency.text,
            transfersInputAccount.text,
            transfersInputTypeAccount.text,
        ).all { !it.isNullOrBlank() }

        transfersButtonNext.isEnabled = allFilled && !hasError
        transfersButtonNext.alpha = if (transfersButtonNext.isEnabled) 1f else 0.5f
    }

    private fun showFieldErrors(errorsList: List<ValidationError>) {
        // limpa erros anteriores
        listOf(layoutBankName, layoutAgency, layoutAccount, layoutAccountType).forEach {

            it.error = null
            it.isErrorEnabled = false
        }

        // marca os erros específicos
        errorsList.forEach { error ->
            when (error) {
                is ValidationError.EmptyBank -> {
                    layoutBankName.error = getString(R.string.error_empty_bank)
                }

                is ValidationError.EmptyAgency -> {
                    layoutAgency.error = getString(R.string.error_empty_agency)
                }

                is ValidationError.EmptyAccount -> {
                    layoutAccount.error = getString(R.string.error_empty_account)
                }

                is ValidationError.InvalidAccount -> {
                    layoutAccount.error = getString(R.string.error_empty_account)
                }

                is ValidationError.EmptyAccountType -> {
                    layoutAccountType.error = getString(R.string.error_empty_account_type)
                }

            }
        }
    }

    private fun showButtonSheetDialog(transfersName: List<BankItem>) {
        val dialog = BottomSheetDialog(this)
        val sheetBinding = CustomBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        val recycler = sheetBinding.recyclerViewBankList
        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = BottomSheetTransfersAdapter(transfersName) { transfersSelected ->
            binding.transfersEditNameBank.text = transfersSelected.displayName
            dialog.dismiss()
        }
        recycler.adapter = adapter

        //  Observa o campo de pesquisa
        sheetBinding.textInputEditText.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString()?.trim().orEmpty()

            val filtered = if (query.isEmpty()) {
                transfersName
            } else {
                val qInt = query.toIntOrNull()
                transfersName.filter { transfers ->
                    transfers.displayName.contains(query, ignoreCase = true) ||
                            (qInt != null && transfers.code == qInt) ||
                            transfers.code.toString().contains(query)
                }
            }

            if (filtered.isEmpty()) {
                // Se nada for encontrado, mostra um item "falso"
                adapter.updateList(
                    listOf(
                        TransfersViewModel.BankItem(
                            code = 0,
                            name = "",
                            displayName = getString(R.string.transfers_list_search_not_find)
                        )
                    )
                )
            } else {
                adapter.updateList(filtered)
            }
        }
        dialog.show()
    }

}