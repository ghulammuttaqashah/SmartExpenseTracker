package com.example.madproject.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.RectF
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.example.madproject.util.DateUtils
import com.example.madproject.util.FormatUtils
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExportManager(private val context: Context) {
    suspend fun exportCsv(expenses: List<Expense>): ExportResult = withContext(Dispatchers.IO) {
        val fileName = "expenses_${timestamp()}.csv"
        val csv = buildString {
            appendLine("Title,Amount,Category,Date,Note")
            expenses.forEach { expense ->
                appendLine(
                    listOf(
                        expense.title.escapeCsv(),
                        expense.amount.toString(),
                        expense.category,
                        DateUtils.formatDate(expense.date),
                        expense.note.orEmpty().escapeCsv()
                    ).joinToString(",")
                )
            }
        }

        writeToDownloads(fileName, "text/csv", csv.toByteArray())
    }

    suspend fun exportPdf(expenses: List<Expense>): ExportResult = withContext(Dispatchers.IO) {
        val fileName = "expenses_${timestamp()}.pdf"
        val document = PdfDocument()
        val paint = Paint().apply { textSize = 12f }
        val legendPaint = Paint().apply { textSize = 11f }
        val titlePaint = Paint().apply { textSize = 16f }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        var y = 40f
        canvas.drawText("Smart Expense Tracker", 40f, y, titlePaint)
        y += 24f
        canvas.drawText("Exported: ${readableTimestamp()}", 40f, y, paint)
        y += 16f

        val categoryTotals = expenses.groupBy { it.category }.mapValues { entry ->
            entry.value.sumOf { it.amount }
        }
        val grandTotal = categoryTotals.values.sum()
        if (grandTotal > 0) {
            val centerX = 140f
            val centerY = 160f
            val radius = 70f
            val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
            var startAngle = -90f

            Category.values().forEach { category ->
                val value = categoryTotals[category.name] ?: 0.0
                if (value <= 0.0) return@forEach
                val sweep = (value / grandTotal * 360.0).toFloat()
                paint.color = categoryColor(category.name)
                canvas.drawArc(rect, startAngle, sweep, true, paint)
                startAngle += sweep
            }

            var legendY = 110f
            Category.values().forEach { category ->
                val value = categoryTotals[category.name] ?: 0.0
                if (value <= 0.0) return@forEach
                paint.color = categoryColor(category.name)
                canvas.drawRect(250f, legendY - 8f, 262f, legendY + 4f, paint)
                canvas.drawText(
                    "${category.label} ${FormatUtils.formatCurrency(value)}",
                    270f,
                    legendY,
                    legendPaint
                )
                legendY += 16f
            }

            y = 260f
        } else {
            y += 16f
        }

        canvas.drawText("Title", 40f, y, paint)
        canvas.drawText("Amount", 200f, y, paint)
        canvas.drawText("Category", 300f, y, paint)
        canvas.drawText("Date", 420f, y, paint)
        y += 18f

        expenses.take(30).forEach { expense ->
            canvas.drawText(expense.title.take(18), 40f, y, paint)
            canvas.drawText(expense.amount.toString(), 200f, y, paint)
            canvas.drawText(expense.category, 300f, y, paint)
            canvas.drawText(DateUtils.formatDate(expense.date), 420f, y, paint)
            y += 18f
        }

        document.finishPage(page)

        val result = writeToDownloads(fileName, "application/pdf", document.toByteArray())
        document.close()
        result
    }

    private fun writeToDownloads(fileName: String, mimeType: String, bytes: ByteArray): ExportResult {
        if (needsLegacyPermission() && !hasLegacyPermission()) {
            return ExportResult(false, "Storage permission required for export on this Android version.")
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return ExportResult(false, "Unable to create export file.")
            resolver.openOutputStream(uri)?.use { stream ->
                stream.write(bytes)
            }
            ExportResult(true, "Saved to Downloads as $fileName")
        } else {
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloads.exists()) {
                downloads.mkdirs()
            }
            val file = File(downloads, fileName)
            FileOutputStream(file).use { stream ->
                stream.write(bytes)
            }
            ExportResult(true, "Saved to Downloads as $fileName")
        }
    }

    private fun needsLegacyPermission(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

    private fun hasLegacyPermission(): Boolean {
        val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        return ContextCompat.checkSelfPermission(context, permission) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun timestamp(): String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

    private fun readableTimestamp(): String = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())

    private fun PdfDocument.toByteArray(): ByteArray {
        val output = java.io.ByteArrayOutputStream()
        writeTo(output)
        return output.toByteArray()
    }

    private fun String.escapeCsv(): String {
        return if (contains(",") || contains("\"") || contains("\n")) {
            "\"" + replace("\"", "\"\"") + "\""
        } else {
            this
        }
    }
}

data class ExportResult(val success: Boolean, val message: String)

private fun categoryColor(category: String): Int {
    return when (category) {
        "Food" -> Color.parseColor("#1F7AE0")
        "Travel" -> Color.parseColor("#2CC1C1")
        "Bills" -> Color.parseColor("#2B2D42")
        "Shopping" -> Color.parseColor("#F72585")
        "Health" -> Color.parseColor("#3CB371")
        else -> Color.parseColor("#90A4AE")
    }
}
