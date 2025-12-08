package com.domleondev.deltabank.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.domleondev.deltabank.presentation.dialogs.MessageAdapter
import com.domleondev.deltabank.repository.geminirepository.GeminiClient
import com.domleondev.deltabank.repository.geminirepository.Message
import com.domleondev.deltabank.repository.request.GeminiContent
import com.domleondev.deltabank.repository.request.GeminiPart
import com.domleondev.deltabank.repository.request.GeminiRequest
import com.domleondev.deltabank.repository.util.ChatStorage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
1. Na tela “Entrar”, tocar em **"Esqueci a senha"**.  
2. Na tela de Identificação, inserir o e-mail e tocar em **"Continuar"**.  
3. Inserir o **código enviado por e-mail** no campo "Código de Verificação" e tocar em **"Continuar"**.  
4. O código expira em **1 minuto**. Se expirar, o usuário pode tocar em **"Reenviar"**.  
5. Digitar a **nova senha** duas vezes e tocar em **"Redefinir"**.  
6. Tocar em **"Fazer Login"** para voltar à tela “Entrar”.

Ao explicar esse fluxo:
- Seja direto, educado e objetivo.
- Nunca invente passos adicionais.
- Nunca entre em detalhes técnicos internos do sistema.

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apaga o arquivo salvo, resetando o chat!!
        // ChatStorage.clearHistory(requireContext())
        // messages.clear()

        recyclerView = view.findViewById(R.id.recyclerView)
        val editMessage = view.findViewById<EditText>(R.id.editMessage)
        val btnSend = view.findViewById<ImageView>(R.id.btnSend)

        // Adapter
        adapter = MessageAdapter(messages)

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

    private fun addChatMessage(text: String, isUser: Boolean) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val todayDate = messages.any { it.text == "[DATE]$today" }

        if (!todayDate) {
            messages.add(Message("[DATE]$today", false))
            adapter.notifyItemInserted(messages.lastIndex)
        }

        messages.add(Message(text, isUser))
        adapter.notifyItemInserted(messages.lastIndex)

        recyclerView.scrollToPosition(messages.lastIndex)
    }

    private fun sendToGemini(userMessage: String) {
        // Fragment-safe coroutine
        viewLifecycleOwner.lifecycleScope.launch {
            val thinkingIndex = messages.size
            addChatMessage("Pensando...", false)

            try {
                val request = GeminiRequest(
                    model = "gemini-2.5-flash",
                    contents = listOf(
                        GeminiContent("user", listOf(GeminiPart(KONTEIN_SYSTEM_PROMPT))),
                        GeminiContent("user", listOf(GeminiPart(userMessage)))
                    )
                )

                val response = GeminiClient.geminiApi.generateContent(request)
                val bot = response.candidates.first().content.parts.first().text

                // remove “Pensando...”
                messages.removeAt(thinkingIndex)
                adapter.notifyItemRemoved(thinkingIndex)

                addChatMessage(bot, false)
                ChatStorage.saveHistory(requireContext(), messages)

            } catch (e: Exception) {
                messages.removeLast()
                adapter.notifyItemRemoved(messages.size)

                addChatMessage("Erro: ${e.message}", false)
            }
        }
    }
}
