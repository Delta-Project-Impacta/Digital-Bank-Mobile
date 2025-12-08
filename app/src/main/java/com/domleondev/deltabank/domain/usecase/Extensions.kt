package com.domleondev.deltabank.domain.usecase

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

val NON_DIGITS = Regex("\\D")
private val NON_DIGIT_REGEX = Regex("[^\\d]")
private val VALID_INPUT_CHARS = Regex("[^\\dX-]")
private val ACCOUNT_VALIDATION_REGEX = Regex("^\\d{1,8}(-[\\dX])?$")

fun TextInputEditText.applyAccountMask() {
    filters = arrayOf(InputFilter.LengthFilter(10)) // 8 dígitos + '-' + 1 DV

    addTextChangedListener(object : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val input = s.toString().uppercase()

            // 🔹 Remove tudo que não for número, X ou hífen
            var clean = input.replace(VALID_INPUT_CHARS, "")

            // 🔹 Garante no máximo 1 hífen
            if (clean.count { it == '-' } > 1) {
                clean = clean.replaceFirst("-", "")
            }

            // 🔹 Se tiver hífen, corta qualquer coisa após 1 caractere do hífen
            if (clean.contains("-")) {
                val parts = clean.split("-")
                val before = parts.getOrNull(0)?.take(8).orEmpty() // até 8 dígitos antes
                val after = parts.getOrNull(1)?.take(1).orEmpty()  // só 1 caractere depois
                clean = "$before-$after"
            } else if (clean.length > 8) {
                // adiciona hífen automático se já passou de 8 dígitos
                clean = clean.substring(0, 8) + "-" + clean.substring(8, 10.coerceAtMost(clean.length))
            }

            // Atualiza apenas se o texto mudou
            if (clean != input) {
                setText(clean)
                setSelection(clean.length)
            }

            // 🔹 Valida formato (1–9 dígitos) + opcional "-" + (1 dígito ou X)
            val isValidFormat = clean.matches(ACCOUNT_VALIDATION_REGEX)
            error = if (isValidFormat || clean.isEmpty()) null
            else "Formato inválido. Ex: 12345678-9"

            isUpdating = false
        }
    })
}