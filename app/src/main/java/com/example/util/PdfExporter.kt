package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.ui.viewmodel.ActiveWorksheet
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun generateAndSharePdf(context: Context, worksheet: ActiveWorksheet, schoolName: String) {
        val pdfFile = generatePdfFile(context, worksheet, schoolName)
        if (pdfFile != null && pdfFile.exists()) {
            sharePdfFile(context, pdfFile, worksheet.title)
        } else {
            Toast.makeText(context, "Failed to generate PDF file.", Toast.LENGTH_SHORT).show()
        }
    }

    fun generateAndPrintPdf(context: Context, worksheet: ActiveWorksheet, schoolName: String) {
        val pdfFile = generatePdfFile(context, worksheet, schoolName)
        if (pdfFile != null && pdfFile.exists()) {
            printPdfFile(context, pdfFile, worksheet.title)
        } else {
            Toast.makeText(context, "Failed to generate PDF for printing.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generatePdfFile(context: Context, worksheet: ActiveWorksheet, schoolName: String): File? {
        val pdfDocument = PdfDocument()

        // Page Specs: Standard A4 width = 595, height = 842 points
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Paints
        val primaryPaint = Paint().apply {
            color = Color.rgb(63, 81, 181) // Indigo Primary
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textHeaderPaint = Paint().apply {
            color = Color.WHITE
            textSize = 14f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val subTitlePaint = Paint().apply {
            color = Color.rgb(100, 116, 139)
            textSize = 12f
            isAntiAlias = true
        }

        val bodyBoldPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val bodyRegularPaint = Paint().apply {
            color = Color.rgb(51, 65, 85)
            textSize = 11f
            isAntiAlias = true
        }

        val borderPaint = Paint().apply {
            color = Color.rgb(203, 213, 225)
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }

        var yPos = 40f
        val xMargin = 40f
        val contentWidth = pageWidth - (xMargin * 2)

        // 1. Top Header Banner Box
        val headerRect = RectF(xMargin, yPos, xMargin + contentWidth, yPos + 40f)
        canvas.drawRoundRect(headerRect, 8f, 8f, primaryPaint)

        val schoolTitle = if (schoolName.isNotBlank()) schoolName.uppercase() else "OAKRIDGE ACADEMY - AI GRAMMAR WORKSHEET"
        canvas.drawText(schoolTitle, xMargin + 16f, yPos + 25f, textHeaderPaint)

        yPos += 60f

        // 2. Title & Metadata
        canvas.drawText(worksheet.title, xMargin, yPos, titlePaint)
        yPos += 20f

        val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val metaText = "Topic: ${worksheet.topic}   |   Level: ${worksheet.difficulty}   |   Date: $formattedDate"
        canvas.drawText(metaText, xMargin, yPos, subTitlePaint)
        yPos += 25f

        // Draw Topic Illustration Picture on PDF
        try {
            val illustrationRes = IllustrationUtils.getTopicIllustrationRes(worksheet.topic)
            val bitmap = BitmapFactory.decodeResource(context.resources, illustrationRes)
            if (bitmap != null) {
                val imgHeight = 85f
                val imgDstRect = RectF(xMargin, yPos, xMargin + contentWidth, yPos + imgHeight)
                val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
                canvas.drawBitmap(bitmap, srcRect, imgDstRect, null)
                yPos += imgHeight + 15f
            }
        } catch (e: Exception) {
            // Fallback gracefully if image cannot be loaded into canvas
        }

        // 3. Student Name Line Box
        val nameBoxRect = RectF(xMargin, yPos, xMargin + contentWidth, yPos + 45f)
        canvas.drawRoundRect(nameBoxRect, 6f, 6f, borderPaint)

        canvas.drawText("Student Name: _______________________________________", xMargin + 12f, yPos + 20f, bodyBoldPaint)
        canvas.drawText("Score: _______ / ${worksheet.questions.size}", xMargin + contentWidth - 120f, yPos + 20f, bodyBoldPaint)
        canvas.drawText("Class / Section: ____________________________________", xMargin + 12f, yPos + 36f, bodyBoldPaint)

        yPos += 65f

        // Page-level footer drawing helper
        val drawPageFooter = { canvasObj: Canvas, pageNum: Int ->
            val footerPaint = Paint().apply {
                color = Color.rgb(148, 163, 184)
                textSize = 9f
                isAntiAlias = true
            }
            canvasObj.drawText("AI-Generated Grammar Worksheet • Page $pageNum", xMargin, 815f, footerPaint)
            canvasObj.drawText("Practice & Learning Session", xMargin + contentWidth - 130f, 815f, footerPaint)
        }

        // 4. Instructions Block
        canvas.drawText("INSTRUCTIONS: Read each question carefully and fill in or select the correct answer.", xMargin, yPos, bodyBoldPaint)
        yPos += 25f

        // 5. Questions Loop
        worksheet.questions.forEachIndexed { index, q ->
            // Pre-calculate exact height needed for this question to prevent layout breaks
            val maxLineChars = 65
            val promptLines = q.questionText.chunked(maxLineChars)
            var requiredHeight = 0f
            
            requiredHeight += promptLines.size * 16f
            if (q.options != null && q.options.isNotEmpty()) {
                requiredHeight += q.options.size * 16f
            } else {
                requiredHeight += 18f
            }
            if (q.hint != null) {
                requiredHeight += 16f
            }
            requiredHeight += 35f // base buffer for label headers and layout spacing

            // Force dynamic page break if item overflows A4 height boundary
            if (yPos > 50f && yPos + requiredHeight > pageHeight - 50f) {
                drawPageFooter(canvas, pageNumber)
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 50f
            }

            val questionNum = "Q${index + 1}. "
            canvas.drawText(questionNum, xMargin, yPos, bodyBoldPaint)

            // Split question text into lines if long
            val textStart = xMargin + 28f

            promptLines.forEachIndexed { lineIdx, line ->
                canvas.drawText(line, if (lineIdx == 0) textStart else xMargin + 28f, yPos, bodyBoldPaint)
                if (lineIdx < promptLines.size - 1) yPos += 16f
            }

            yPos += 18f

            // Options or Answer Blank Lines
            if (q.options != null && q.options.isNotEmpty()) {
                q.options.forEachIndexed { optIdx, opt ->
                    val optLetter = ('A' + optIdx).toString()
                    val optText = "   [   ] $optLetter) $opt"
                    canvas.drawText(optText, xMargin + 20f, yPos, bodyRegularPaint)
                    yPos += 16f
                }
            } else {
                canvas.drawText("Answer: ________________________________________________________________", xMargin + 20f, yPos, bodyRegularPaint)
                yPos += 18f
            }

            if (q.hint != null) {
                canvas.drawText("Hint: ${q.hint}", xMargin + 20f, yPos, subTitlePaint)
                yPos += 16f
            }

            yPos += 12f
        }

        // 6. Answer Key Footer / Section if present
        val hasExplanations = worksheet.questions.any { !it.explanation.isNullOrBlank() }
        if (hasExplanations) {
            if (yPos > pageHeight - 120f) {
                drawPageFooter(canvas, pageNumber)
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 50f
            }

            yPos += 15f
            canvas.drawLine(xMargin, yPos, xMargin + contentWidth, yPos, borderPaint)
            yPos += 20f

            canvas.drawText("TEACHER ANSWER KEY & EXPLANATIONS", xMargin, yPos, titlePaint)
            yPos += 20f

            worksheet.questions.forEachIndexed { index, q ->
                // Calculate item height for answer key
                var requiredItemHeight = 16f
                if (!q.explanation.isNullOrBlank()) {
                    val explanationLines = q.explanation.chunked(70)
                    requiredItemHeight += explanationLines.size * 18f
                }
                requiredItemHeight += 15f

                if (yPos > 50f && yPos + requiredItemHeight > pageHeight - 50f) {
                    drawPageFooter(canvas, pageNumber)
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = 50f
                }

                val keyText = "Q${index + 1}: Correct Answer -> ${q.correctAnswer}"
                canvas.drawText(keyText, xMargin, yPos, bodyBoldPaint)
                yPos += 16f

                if (!q.explanation.isNullOrBlank()) {
                    val explanationText = "Rule: ${q.explanation}"
                    canvas.drawText(explanationText, xMargin + 10f, yPos, bodyRegularPaint)
                    yPos += 18f
                }
            }
        }

        drawPageFooter(canvas, pageNumber)
        pdfDocument.finishPage(page)

        // Save file to cache directory
        return try {
            val fileDir = File(context.cacheDir, "pdf_exports")
            if (!fileDir.exists()) fileDir.mkdirs()

            val sanitizedTitle = worksheet.title.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val pdfFile = File(fileDir, "Worksheet_${sanitizedTitle}.pdf")
            if (pdfFile.exists()) pdfFile.delete()

            val fos = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    private fun sharePdfFile(context: Context, file: File, title: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Grammar Worksheet: $title")
                putExtra(Intent.EXTRA_TEXT, "Attached is the AI Grammar Worksheet: $title")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Export Worksheet PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun printPdfFile(context: Context, file: File, title: String) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager != null) {
                val printAdapter: PrintDocumentAdapter = PdfPrintDocumentAdapter(file)
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()

                printManager.print("Worksheet_$title", printAdapter, printAttributes)
            } else {
                sharePdfFile(context, file, title)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sharePdfFile(context, file, title)
        }
    }
}

class PdfPrintDocumentAdapter(private val pdfFile: File) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: android.os.Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        val info = android.print.PrintDocumentInfo.Builder("worksheet.pdf")
            .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build()

        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out android.print.PageRange>?,
        destination: android.os.ParcelFileDescriptor?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        var input: java.io.InputStream? = null
        var output: java.io.OutputStream? = null

        try {
            input = java.io.FileInputStream(pdfFile)
            output = java.io.FileOutputStream(destination?.fileDescriptor)

            val buf = ByteArray(1024)
            var bytesRead: Int
            while (input.read(buf).also { bytesRead = it } > 0) {
                output.write(buf, 0, bytesRead)
            }

            callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback?.onWriteFailed(e.message)
        } finally {
            try {
                input?.close()
                output?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
