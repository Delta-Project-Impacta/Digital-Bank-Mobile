package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.dialogs.BottomSheetAdapter
import com.domleondev.deltabank.repository.response.CityResponse
import com.domleondev.deltabank.repository.response.StateResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlin.getValue
import com.domleondev.deltabank.viewModel.RegisterAddressViewModel

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

import android.graphics.Color
import android.os.Build
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class RegisterAddressActivity : AppCompatActivity() {

    private val viewModel: RegisterAddressViewModel by viewModels()

    private lateinit var buttonAddressNext: AppCompatButton
    private lateinit var inputAddressCep: TextInputEditText
    private lateinit var inputAddressEndereco: TextInputEditText
    private lateinit var inputAddressNumero: TextInputEditText
    private lateinit var inputAddressComplemento: TextInputEditText
    private lateinit var inputAddressBairro: TextInputEditText
    private lateinit var inputAddressCidade: TextView
    private lateinit var inputAddressEstado: TextView
    private lateinit var registerAddressImgBack: ImageView
    private lateinit var progressBar: ProgressBar
    private var cepValido = false
    private var email: String? = null
    private var name: String? = null
    private var cpf: String? = null
    private var birth: String? = null
    private var buscaCepJob: Job? = null // Variável para controlar o cancelamento


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_address)

        // --- Recebe o email e o nome da tela anterior ---
        email = intent.getStringExtra("email")
        name = intent.getStringExtra("name")
        cpf = intent.getStringExtra("cpf")
        birth = intent.getStringExtra("birth")

        if (email.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.error_email_missing), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (cpf.isNullOrBlank()) {
            Toast.makeText(this, "CPF ausente", Toast.LENGTH_SHORT).show() //Cadastrar na String dps
            finish()
            return
        }

        // --- Bindings ---
        buttonAddressNext = findViewById(R.id.address_Button_Next)
        inputAddressCep = findViewById(R.id.address_Edit_Cep)
        inputAddressEndereco = findViewById(R.id.address_Edit)
        inputAddressNumero = findViewById(R.id.address_Edit_Number)
        inputAddressComplemento = findViewById(R.id.address_Edit_Complement)
        inputAddressBairro = findViewById(R.id.address_Edit_District)
        inputAddressCidade = findViewById(R.id.address_Edit_City)
        inputAddressEstado = findViewById(R.id.address_Edit_State)
        registerAddressImgBack = findViewById(R.id.address_Button_Back)
        progressBar = findViewById(R.id.progressBar)

        setupObservers()
        registerAddressImgBack.setOnClickListener { finish() }

        // --- Filtros e máscara do CEP ---
        aplicarMascaraECepFilter()
        configurarBuscaCep()

        inputAddressEstado.setOnClickListener { viewModel.buscarEstados() }

        inputAddressCidade.setOnClickListener {
            val uf = inputAddressEstado.text.toString()
            if (uf.isEmpty()) {
                showAlert("Selecione primeiro o estado.")
            } else {
                viewModel.buscarCidades(uf)
            }
        }

        buttonAddressNext.setOnClickListener {
            val cep = inputAddressCep.text.toString().replace("-", "").trim()
            when {
                cep.length < 8 -> showAlert("Digite os 8 dígitos do CEP.")
                !cepValido -> showAlert("CEP inválido, tente novamente.")
                else -> {
                    viewModel.validarCampos(
                        inputAddressCep.text.toString(),
                        inputAddressEndereco.text.toString(),
                        inputAddressNumero.text.toString(),
                        inputAddressCidade.text.toString(),
                        inputAddressEstado.text.toString()
                    )
                }
            }
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
        val headerContainer = findViewById<View>(R.id.address_Toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.address_root)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            headerContainer.setPadding(0, bars.top, 0, 0)
            insets
        }
    }

    private fun aplicarMascaraECepFilter() {
        val numbersAndDashFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.isEmpty()) return@InputFilter source
            val filtered = source.filter { it.isDigit() || it == '-' }
            if (filtered.length != source.length) {
                Toast.makeText(this, "Caractere não permitido!", Toast.LENGTH_SHORT).show()
            }
            filtered.toString()
        }

        inputAddressCep.filters = arrayOf(numbersAndDashFilter)
        inputAddressCep.addTextChangedListener(object : TextWatcher {
                    private var isUpdating = false
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if (isUpdating) return
                        isUpdating = true

                        val clean = s.toString().replace(Regex("[^\\d]"), "")
                        val formatted = StringBuilder()
                        val digits = if (clean.length > 8) clean.substring(0, 8) else clean

                        for (i in digits.indices) {
                            formatted.append(digits[i])
                            if (i == 4 && i < digits.length - 1) formatted.append("-")
                        }

                        val masked = formatted.toString()
                        inputAddressCep.setText(masked)
                        inputAddressCep.setSelection(masked.length)
                        isUpdating = false
                    }
                })
    }

    private fun configurarBuscaCep() {
        inputAddressCep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 1. Cancela a busca anterior se ela ainda não aconteceu (Debounce)
                buscaCepJob?.cancel()

                // 2. Cria uma nova espera de 500ms
                buscaCepJob = lifecycleScope.launch {
                    delay(500) // Espera meio segundo para ver se a máscara ou o usuário vai digitar mais algo

                    val cep = s.toString().replace("-", "").trim()

                    // 3. Só chama se tiver 8 dígitos e for diferente da última busca (opcional)
                    if (cep.length == 8) {
                        viewModel.buscarCep(cep)
                    } else {
                        cepValido = false
                        // Opcional: Limpar campos se o usuário apagar o CEP
                    }
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.error.observe(this) { message ->
            if (!message.isNullOrBlank()) showAlert(message)
        }

        viewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.viaCepData.observe(this) { endereco ->
            if (endereco != null && !endereco.cep.isNullOrEmpty()) {
                cepValido = true
                inputAddressEndereco.setText(endereco.logradouro ?: "")
                inputAddressBairro.setText(endereco.bairro ?: "")
                inputAddressCidade.text = endereco.localidade ?: ""
                inputAddressEstado.text = endereco.uf ?: ""

                inputAddressCidade.isEnabled = false
                inputAddressEstado.isEnabled = false
                inputAddressCidade.alpha = 0.6f
                inputAddressEstado.alpha = 0.6f

            } else {
                cepValido = false
                showAlert("CEP inválido, tente novamente.")
                inputAddressCidade.isEnabled = true
                inputAddressEstado.isEnabled = true
                inputAddressCidade.alpha = 1f
                inputAddressEstado.alpha = 1f
            }
        }

        viewModel.allValid.observe(this) { valid ->
            if (valid) {
                val intent = Intent(this, RegisterLoginPasswordActivity::class.java)
                intent.putExtra(RegisterLoginPasswordActivity.EXTRA_EMAIL, email)
                intent.putExtra(RegisterLoginPasswordActivity.EXTRA_NOME, name)
                intent.putExtra(RegisterLoginPasswordActivity.EXTRA_CPF, cpf)
                intent.putExtra(RegisterLoginPasswordActivity.EXTRA_BIRTH, birth)
                startActivity(intent)
            }
        }

        viewModel.estados.observe(this) { estados -> showBottomSheetEstados(estados) }
        viewModel.cidades.observe(this) { cidades -> showBottomSheetCidades(cidades) }
    }

    private fun showBottomSheetEstados(estados: List<StateResponse>) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottomsheet_list, null)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = BottomSheetAdapter(estados.map { it.sigla }) { estadoSelecionado ->
            inputAddressEstado.text = estadoSelecionado
            inputAddressCidade.text = ""
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showBottomSheetCidades(cidades: List<CityResponse>) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottomsheet_list, null)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = BottomSheetAdapter(cidades.map { it.nome }) { cidadeSelecionada ->
            inputAddressCidade.text = cidadeSelecionada
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Erro de validação")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
