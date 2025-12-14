package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.domleondev.deltabank.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect

class TransferHomeActivity : AppCompatActivity() {

    private val TAG = "TRANSFER_HOME"
    private val vm: com.domleondev.deltabank.viewModel.TransferHomeViewModel by viewModels()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transfer_home)

        val backArrow = findViewById<ImageView>(R.id.transfer_Home_Arrow_Back)
        backArrow.setOnClickListener {
            Log.d(TAG, "Botão voltar pressionado - finalizando activity")
            finish()
        }

        val transferHomePasswordButtonNext = findViewById<AppCompatButton>(R.id.transfer_Home_Button_Next)
        val transferHomeLayoutAccount = findViewById<LinearLayout>(R.id.transfer_Home_Layout_Account)
        transferHomeLayoutAccount.setOnClickListener {
            Log.d(TAG, "Clicou em Transfer Home Layout Account -> abrindo TransferDataActivity")
            intent = Intent(this, TransferDataActivity::class.java)
            startActivity(intent)
        }
        //  Configuração universal de status bar transparente (NÃO MUDAR)
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
                window.navigationBarColor = Color.TRANSPARENT
            }
        }
        val headerContainer = findViewById<View>(R.id.transfer_Home_Container)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transfer_Home)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            headerContainer.setPadding(0, bars.top, 0, 0)
            insets
        }

        val pixInput = findViewById<TextInputEditText>(R.id.transfer_Home_Edit_Pix)

        // Observers: events (navigation / toast)
        lifecycleScope.launchWhenStarted {
            vm.events.collect { ev ->
                when (ev) {
                    is com.domleondev.deltabank.viewModel.TransferHomeViewModel.Event.NavigateToAmount -> {
                        Log.d(TAG, "Evento: NavigateToAmount recebido -> uid=${ev.uid} name=${ev.name} cpf=${ev.cpf} balance=${ev.senderBalance}")
                        val intent = Intent(this@TransferHomeActivity, PixAmountActivity::class.java).apply {
                            putExtra("recipientUid", ev.uid)
                            putExtra("recipientName", ev.name)
                            putExtra("recipientCpf", ev.cpf)
                            putExtra("senderBalance", ev.senderBalance)
                        }
                        startActivity(intent)
                    }
                    is com.domleondev.deltabank.viewModel.TransferHomeViewModel.Event.ShowToast -> {
                        Log.d(TAG, "Evento: ShowToast -> ${ev.message}")
                        Toast.makeText(this@TransferHomeActivity, ev.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Mantive o log original do clique e deleguei a ação ao ViewModel
        transferHomePasswordButtonNext.setOnClickListener {
            val rawCpf = pixInput.text?.toString() ?: ""
            Log.d(TAG, "Clicou Next — delegando para ViewModel. CPF raw='$rawCpf'")
            vm.onNextClicked(rawCpf)

            // --- Fallback direto: busca no Firestore e navega caso encontre (garante que a tela abra) ---
            val cpf = normalizeCpf(rawCpf)
            if (cpf.isBlank()) {
                Log.d(TAG, "CPF vazio após normalização — não fará fallback.")
                return@setOnClickListener
            }

            Log.d(TAG, "Fallback: buscando usuário por CPF diretamente no Firestore -> $cpf")
            db.collection("users")
                .whereEqualTo("cpf", cpf)
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        Log.w(TAG, "Fallback: usuário não encontrado para cpf=$cpf")
                        // não toastar aqui porque o VM pode exibir mensagem via evento
                        return@addOnSuccessListener
                    }

                    val doc = snapshot.documents[0]
                    val recipientUid = doc.id
                    val recipientName = doc.getString("name") ?: ""
                    val recipientCpf = doc.getString("cpf") ?: ""
                    Log.d(TAG, "Fallback: destinatário encontrado -> uid=$recipientUid name=$recipientName cpf=$recipientCpf")

                    // Puxa balance do remetente (usuario autenticado) — se desejar manter como no VM
                    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    if (currentUser == null) {
                        Log.e(TAG, "Fallback: usuário não autenticado ao tentar transferir")
                        return@addOnSuccessListener
                    }

                    db.collection("users").document(currentUser.uid).get()
                        .addOnSuccessListener { senderDoc ->
                            val senderBalance = senderDoc.getDouble("balance") ?: 0.0
                            Log.d(TAG, "Fallback: balance do remetente (uid=${currentUser.uid}) = $senderBalance")

                            val intent = Intent(this@TransferHomeActivity, PixAmountActivity::class.java).apply {
                                putExtra("recipientUid", recipientUid)
                                putExtra("recipientName", recipientName)
                                putExtra("recipientCpf", recipientCpf)
                                putExtra("senderBalance", senderBalance)
                            }
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Fallback: erro ao buscar balance do remetente", e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Fallback: erro buscando usuário por CPF", e)
                }
        }
    }

    private fun normalizeCpf(input: String): String {
        return input.replace(Regex("[^0-9]"), "")
    }
}
