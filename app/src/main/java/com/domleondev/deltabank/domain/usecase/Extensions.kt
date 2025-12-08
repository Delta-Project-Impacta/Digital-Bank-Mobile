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

// Máscara de data — formato dd/MM/yyyy
fun TextInputEditText.addDateMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false
        private var oldText = ""

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            oldText = s?.toString() ?: ""
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val newText = s?.toString() ?: ""

            // Detecta se o usuário está apagando
            val isDeleting = oldText.length > newText.length

            // Remove tudo que não é número
            val clean = newText.replace(NON_DIGIT_REGEX, "")

            val formatted = StringBuilder()
            for (i in clean.indices) {
                formatted.append(clean[i])
                if ((i == 1 || i == 3) && i != clean.lastIndex) {
                    formatted.append('/')
                }
            }

            val masked = if (formatted.length > 10) formatted.substring(0, 10) else formatted.toString()

            // Atualiza o texto apenas se ele for diferente
            if (masked != newText) {
                this@addDateMask.setText(masked)
                // Reposiciona o cursor corretamente
                this@addDateMask.setSelection(
                    if (isDeleting) masked.length.coerceAtMost(newText.length)
                    else masked.length
                )
            }

            isUpdating = false
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}

// Máscara de CPF — formato 000.000.000-00
fun TextInputEditText.addCpfMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false
        private val mask = "###.###.###-##"

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val clean = s.toString().replace(NON_DIGIT_REGEX, "") // remove tudo que não é dígito

            var masked = ""
            var i = 0
            for (m in mask.toCharArray()) {
                if (m == '#') {
                    if (i >= clean.length) break
                    masked += clean[i]
                    i++
                } else {
                    if (i < clean.length) masked += m
                }
            }

            // Só atualiza se o texto realmente mudou
            if (masked != s.toString()) {
                setText(masked)
                setSelection(masked.length)
            }

            isUpdating = false
        }
    })
}

// Máscara de phone — (00) 00000-0000
fun TextInputEditText.addPhoneMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false
        private val mask = "(##) #####-####"

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val clean = s.toString().replace(NON_DIGIT_REGEX, "")

            var masked = ""
            var i = 0
            for (m in mask.toCharArray()) {
                if (m == '#') {
                    if (i >= clean.length) break
                    masked += clean[i]
                    i++
                } else {
                    if (i < clean.length) masked += m
                }
            }

            if (masked != s.toString()) {
                setText(masked)
                setSelection(masked.length)
            }

            isUpdating = false
        }
    })
}

// Máscara de CEP — 00000-000
fun TextInputEditText.addCepMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (isUpdating) return
            isUpdating = true

            val clean = s.toString().replace(NON_DIGITS, "")
            val formatted = StringBuilder()
            val digits = if (clean.length > 8) clean.substring(0, 8) else clean

            for (i in digits.indices) {
                formatted.append(digits[i])
                if (i == 4 && i < digits.length - 1) formatted.append("-")
            }

            val masked = formatted.toString()
            this@addCepMask.setText(masked)
            this@addCepMask.setSelection(masked.length)
            isUpdating = false
        }
    })

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
                    clean = clean.substring(0, 8) + "-" + clean.substring(
                        8,
                        10.coerceAtMost(clean.length)
                    )
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

    // Máscara de Número de Cartão — formato 0000 0000 0000 0000
    fun TextInputEditText.addCardNumberMask() {
        this.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                // remove tudo que não for número
                val clean = s.toString().replace(NON_DIGITS, "")

                val formatted = StringBuilder()
                for (i in clean.indices) {
                    formatted.append(clean[i])
                    // adiciona espaço a cada 4 dígitos, exceto no final
                    if ((i + 1) % 4 == 0 && i + 1 < clean.length) {
                        formatted.append(" ")
                    }
                }

                // atualiza o texto no input
                val masked = formatted.toString()
                if (masked != s.toString()) {
                    this@addCardNumberMask.setText(masked)
                    this@addCardNumberMask.setSelection(masked.length) // coloca o cursor no final
                }

                isUpdating = false
            }
        })
    }

    // Máscara de data — formato MM/yyyy
    fun TextInputEditText.addDateValidityMask() {
        this.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private var oldText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                oldText = s?.toString() ?: ""
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true

                val newText = s?.toString() ?: ""

                // Detecta se o usuário está apagando
                val isDeleting = oldText.length > newText.length

                // Remove tudo que não é número
                val clean = newText.replace(NON_DIGIT_REGEX, "")

                val formatted = StringBuilder()
                for (i in clean.indices) {
                    formatted.append(clean[i])
                    if ((i == 1) && i != clean.lastIndex) {
                        formatted.append('/')
                    }
                }

                val masked =
                    if (formatted.length > 5) formatted.substring(0, 5) else formatted.toString()

                // Atualiza o texto apenas se ele for diferente
                if (masked != newText) {
                    this@addDateValidityMask.setText(masked)
                    // Reposiciona o cursor corretamente
                    this@addDateValidityMask.setSelection(
                        if (isDeleting) masked.length.coerceAtMost(newText.length)
                        else masked.length
                    )
                }

                isUpdating = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}