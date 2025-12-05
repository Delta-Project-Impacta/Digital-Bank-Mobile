package com.domleondev.deltabank.presentation.usecase

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ValidateBirthDateUseCase {

    private val LENGTH = 10
    operator fun invoke(date: String): Boolean {
        if (date.isBlank()) return false
        if (date.length != LENGTH ) return false

        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false // evita aceitar datas inválidas, como 32/13/2022
            val birthDate: Date = sdf.parse(date) ?: return false

            val birthCalendar = Calendar.getInstance().apply { time = birthDate }
            val today = Calendar.getInstance()

            var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            age >= 18
        } catch (e: Exception) {
            false
        }
    }
}
