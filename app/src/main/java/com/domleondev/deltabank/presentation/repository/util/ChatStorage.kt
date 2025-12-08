package com.domleondev.deltabank.presentation.repository.util

import android.content.Context
import com.domleondev.deltabank.presentation.repository.geminirepository.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object ChatStorage {

    private const val FILE_NAME = "chat_history.json"

    fun saveHistory(context: Context, messages: List<Message>) {
        val gson = Gson()
        val json = gson.toJson(messages)

        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json)
    }

    fun loadHistory(context: Context): MutableList<Message> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return mutableListOf()

        return try {
            val json = file.readText()
            val type = object : TypeToken<MutableList<Message>>() {}.type
            Gson().fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    // Apaga o histórico! Usar se precisar resetar o chat.
    fun clearHistory(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) {
            file.delete()
        }
    }
}