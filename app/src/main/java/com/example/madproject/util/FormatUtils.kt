package com.example.madproject.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object FormatUtils {
    fun formatCurrency(amount: Double): String {
        val locale = Locale("en", "PK")
        val formatter = NumberFormat.getCurrencyInstance(locale)
        formatter.currency = Currency.getInstance("PKR")
        val formatted = formatter.format(amount)
        return formatted.replace("PKR", "Rs ")
    }
}
