package com.example.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.text.HtmlCompat

class EditTextDetailActivity : AppCompatActivity() {
    private lateinit var edtContent: com.google.android.material.textfield.TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_text_detail)

        edtContent = findViewById(R.id.edtContent)

        edtContent.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                // Keep the text selection capability available (selection cursor)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                // Prevent the menu from appearing
                menu?.clear()
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                // Perform cleanup if needed
            }
        }

        val oldContent = intent.getStringExtra("content")
        if(oldContent != null) {
            edtContent.setText(HtmlCompat.fromHtml(oldContent, HtmlCompat.FROM_HTML_MODE_LEGACY))
        }

        val btnBold = findViewById<ImageButton>(R.id.btnBold)
        btnBold.setOnClickListener {
            boldText()
        }

        val btnItalic = findViewById<ImageButton>(R.id.btnItalic)
        btnItalic.setOnClickListener {
            italicText()
        }

        val btnUnderline = findViewById<ImageButton>(R.id.btnUnderline)
        btnUnderline.setOnClickListener {
            underlineText()
        }

        val btnStrike = findViewById<ImageButton>(R.id.btnStrike)
        btnStrike.setOnClickListener {
            strikeText()
        }

        val btnRemoveFomat = findViewById<ImageButton>(R.id.btnRemoveFormat)
        btnRemoveFomat.setOnClickListener {
            removeFormat()
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            finish()
        }

        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            if(edtContent.text.toString().isEmpty()) {
                Toast.makeText(this, "Nội dung không được để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            intent.putExtra("content",
                edtContent.text?.let { it1 -> HtmlCompat.toHtml(it1, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE) })
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun boldText() {
        val start = edtContent.selectionStart
        val end = edtContent.selectionEnd
        if(start == end) {
            return
        }
        val spannableString = SpannableStringBuilder(edtContent.text)
        spannableString.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            start,
            end,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        edtContent.text = spannableString

        // reset selection
        edtContent.setSelection(start, end)
    }

    private fun italicText() {
        val start = edtContent.selectionStart
        val end = edtContent.selectionEnd
        if(start == end) {
            return
        }
        val spannableString = SpannableStringBuilder(edtContent.text)
        spannableString.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.ITALIC),
            edtContent.selectionStart,
            edtContent.selectionEnd,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        edtContent.text = spannableString

        // reset selection
        edtContent.setSelection(start, end)
    }

    private fun underlineText() {
        val start = edtContent.selectionStart
        val end = edtContent.selectionEnd
        if(start == end) {
            return
        }
        val spannableString = SpannableStringBuilder(edtContent.text)
        spannableString.setSpan(
            android.text.style.UnderlineSpan(),
            edtContent.selectionStart,
            edtContent.selectionEnd,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        edtContent.text = spannableString

        // reset selection
        edtContent.setSelection(start, end)
    }

    private fun strikeText() {
        val start = edtContent.selectionStart
        val end = edtContent.selectionEnd
        if(start == end) {
            return
        }
        val spannableString = SpannableStringBuilder(edtContent.text)
        spannableString.setSpan(
            android.text.style.StrikethroughSpan(),
            edtContent.selectionStart,
            edtContent.selectionEnd,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        edtContent.text = spannableString

        // reset selection
        edtContent.setSelection(start, end)
    }

    private fun removeFormat() {
        val start = edtContent.selectionStart
        val end = edtContent.selectionEnd
        if(start == end) {
            return
        }
        val spannableString = SpannableStringBuilder(edtContent.text)
        val getSpan = edtContent.text?.getSpans(edtContent.selectionStart, edtContent.selectionEnd, Any::class.java)
        if(getSpan != null) {
            for (span in getSpan) {
                spannableString.removeSpan(span)
            }
        }
        edtContent.text = spannableString

        // reset selection
        edtContent.setSelection(start, end)
    }
}