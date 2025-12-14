package com.domleondev.deltabank.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.domleondev.deltabank.presentation.dialogs.MessageAdapter
import com.domleondev.deltabank.repository.geminirepository.GeminiClient
import com.domleondev.deltabank.repository.geminirepository.Message
import com.domleondev.deltabank.presentation.activities.ForgotPasswordActivity
import com.google.firebase.auth.FirebaseAuth
import com.domleondev.deltabank.repository.request.GeminiContent
import com.domleondev.deltabank.repository.request.GeminiPart
import com.domleondev.deltabank.repository.request.GeminiRequest
import com.domleondev.deltabank.repository.util.ChatStorage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val ACTION_TAG = "[ACTION:RESET_PASSWORD_BUTTON]"
private const val ACTION_ID_RESET_PASSWORD = "RESET_PASSWORD"

private const val KONTEIN_SYSTEM_PROMPT = """
Você é o Kontein, assistente virtual do Banco Delta.
Seu comportamento varia de acordo com o tipo de pergunta do usuário. 
Siga estritamente as regras abaixo.

====================  
🎯 QUANDO USAR O FLUXO DE RECUPERAÇÃO DE SENHA  
====================
Use o fluxo abaixo **somente** quando o usuário pedir ajuda com:
- “esqueci minha senha”
- “recuperar senha”
- “resetar senha”
- “não lembro minha senha”
- ou qualquer variação claramente relacionada à Recuperação de Senha.

Fluxo oficial da Recuperação de Senha:

<br><br>1. Na tela <b>“Entrar”</b>, tocar em <b><font color="#FF5800">"Esqueci a senha"</font></b>.<br>
<br>
2. Na tela de <b>Identificação</b>, inserir o e-mail e tocar em <b><font color="#FF5800">"Continuar"</font></b>.<br>
<br>
3. Inserir o <b>código enviado por e-mail</b> no campo <b>"Código de Verificação"</b> e tocar em <b><font color="#FF5800">"Continuar"</font></b>.<br>
<br>
4. O código expira em <b>1 minuto</b>. Se expirar, o usuário pode tocar em <b><font color="#FF5800">"Reenviar"</font></b>.<br>
<br>
5. Digitar a <b>nova senha</b> duas vezes e tocar em <b><font color="#FF5800">"Redefinir"</font></b>.<br>
<br>
6. Tocar em <b><font color="#FF5800">"Fazer Login"</font></b> para voltar à tela <b>“Entrar”</b>.<br>

Ao explicar esse fluxo dizer no final:
"<br>Se quiser, posso te ajudar a iniciar esse processo agora, basta tocar no botão abaixo para ser redirecionado com segurança.<br>"
- Quando explicar fluxos, use <br> para quebrar linhas.
- Seja direto, educado e objetivo.
- Nunca invente passos adicionais.
- Nunca entre em detalhes técnicos internos do sistema.
- **IMPORTANTE:** Ao finalizar a explicação, anexe a tag [ACTION:RESET_PASSWORD_BUTTON] na última linha da sua resposta.

====================  
💬 PERGUNTAS COMUNS (conversa casual)  
====================
Se o usuário fizer perguntas genéricas como:
- “está tudo bem?”
- “como você está?”
- “o que você sabe fazer?”
…então responda de forma leve e amigável **sem falar de Recuperação de Senha**.

====================  
❔ PERGUNTAS NÃO RELACIONADAS  
====================
Para perguntas que **não são** sobre:
- Recuperação de Senha (fluxo acima)
- Conversa comum

Responda de forma curta e educada, mas:
- Sem mencionar serviços do banco.
- Sem explicar produtos do banco.
- Sem dar instruções operacionais.

====================  
❌ RESTRIÇÕES ABSOLUTAS  
====================
Você **nunca** deve:
- Executar, simular ou sugerir operações financeiras.
- Solicitar dados pessoais sensíveis.
- Falar sobre outros serviços do banco que não sejam a recuperação de senha.
- Sair do seu papel de assistente controlado e seguro.

"""

class FragmentChat : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatRoot = view.findViewById<View>(R.id.chatRoot)

        ViewCompat.setOnApplyWindowInsetsListener(chatRoot) { v, insets ->

            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                imeInsets.bottom
            )

            insets
        }

        auth = FirebaseAuth.getInstance()

        // Apaga o arquivo salvo, resetando o chat!!
        // ChatStorage.clearHistory(requireContext())
        // messages.clear()

        recyclerView = view.findViewById(R.id.recyclerView)
        val editMessage = view.findViewById<EditText>(R.id.editMessage)
        val btnSend = view.findViewById<ImageView>(R.id.btnSend)

        // ********************************************************
        // 1. Definição do Callback de Clique do Botão (COM LOGOUT)
        // ********************************************************
        val onButtonClick: (actionId: String) -> Unit = { actionId ->
            if (actionId == ACTION_ID_RESET_PASSWORD) {

                auth.signOut()
                Toast.makeText(requireContext(), "Logout realizado", Toast.LENGTH_SHORT).show()

                val intent = Intent(requireContext(), ForgotPasswordActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                requireActivity().finish()
            }
        }

        // ********************************************************
        // 2. Inicialização do Adapter com o Callback
        // ********************************************************
        adapter = MessageAdapter(messages, onButtonClick)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = false
                reverseLayout = false
            }
            adapter = this@FragmentChat.adapter
            itemAnimator = null
        }

        // ---- Carrega histórico salvo ----
        val saved = ChatStorage.loadHistory(requireContext())
        if (saved.isNotEmpty()) {
            messages.addAll(saved)
            adapter.notifyItemRangeInserted(0, saved.size)
            recyclerView.scrollToPosition(messages.lastIndex)
        } else {
            addChatMessage(
                getString(R.string.chat_bot_welcome),
                isUser = false
            )
            ChatStorage.saveHistory(requireContext(), messages)
        }

        // ---- Botão enviar ----
        btnSend.setOnClickListener {
            val userText = editMessage.text.toString().trim()
            if (userText.isNotEmpty()) {
                addChatMessage(userText, isUser = true)
                ChatStorage.saveHistory(requireContext(), messages)
                editMessage.text.clear()
                sendToGemini(userText)
            }
        }
    }

    // ********************************************************
    // 3. Função addChatMessage
    // ********************************************************
    private fun addChatMessage(
        text: String,
        isUser: Boolean,
        isButton: Boolean = false,
        actionId: String? = null
    ) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val todayDate = messages.any { it.text == "[DATE]$today" }

        if (!todayDate) {
            messages.add(Message("[DATE]$today", isSentByUser = false))
            adapter.notifyItemInserted(messages.lastIndex)
        }

        // Cria o objeto Message completo
        messages.add(
            Message(
                text = text,
                isSentByUser = isUser,
                isButton = isButton,
                actionId = actionId
            )
        )

        adapter.notifyItemInserted(messages.lastIndex)
        recyclerView.scrollToPosition(messages.lastIndex)
    }


    private fun sendToGemini(userMessage: String) {
        // Fragment-safe coroutine
        viewLifecycleOwner.lifecycleScope.launch {
            val thinkingIndex = messages.size
            // Chamada de addChatMessage para "Pensando..."
            addChatMessage("Pensando...", isUser = false)

            try {
                // ... (Requisição ao Gemini)
                val request = GeminiRequest(
                    model = "gemini-2.5-flash",
                    contents = listOf(
                        GeminiContent("user", listOf(GeminiPart(KONTEIN_SYSTEM_PROMPT))),
                        GeminiContent("user", listOf(GeminiPart(userMessage)))
                    )
                )

                val response = GeminiClient.geminiApi.generateContent(request)
                var botResponseText = response.candidates.first().content.parts.first().text

                // remove “Pensando...”
                messages.removeAt(thinkingIndex)
                adapter.notifyItemRemoved(thinkingIndex)

                // ********************************************************
                // 4. Lógica de Detecção do Botão
                // ********************************************************
                val shouldShowButton = botResponseText.contains(ACTION_TAG)

                if (shouldShowButton) {
                    // Remove a tag do texto final da mensagem
                    botResponseText = botResponseText.replace(ACTION_TAG, "").trim()
                }

                // Adiciona a mensagem final do bot, setando as flags se o botão for necessário
                addChatMessage(
                    text = botResponseText,
                    isUser = false,
                    isButton = shouldShowButton,
                    actionId = if (shouldShowButton) ACTION_ID_RESET_PASSWORD else null
                )

                ChatStorage.saveHistory(requireContext(), messages)

            } catch (e: Exception) {
                if (messages.size > thinkingIndex && messages[thinkingIndex].text == "Pensando...") {
                    messages.removeAt(thinkingIndex)
                    adapter.notifyItemRemoved(thinkingIndex)
                }

                addChatMessage("Erro: ${e.message}", isUser = false)
            }
        }
    }
}