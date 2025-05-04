package com.example.studentm

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.content.Intent

import android.widget.PopupMenu

class UpdateStudentActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtMSSV: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_student)

        edtName = findViewById(R.id.edtName)
        edtMSSV = findViewById(R.id.edtMSSV)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        btnSave = findViewById(R.id.btnSave)

        edtName.setText(intent.getStringExtra("name"))
        edtMSSV.setText(intent.getStringExtra("mssv"))
        edtEmail.setText(intent.getStringExtra("email"))
        edtPhone.setText(intent.getStringExtra("phone"))

        btnSave.setOnClickListener {
            val updatedName = edtName.text.toString()
            val updatedMSSV = edtMSSV.text.toString()
            val updatedEmail = edtEmail.text.toString()
            val updatedPhone = edtPhone.text.toString()

            val resultIntent = Intent().apply {
                putExtra("name", updatedName)
                putExtra("mssv", updatedMSSV)
                putExtra("email", updatedEmail)
                putExtra("phone", updatedPhone)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
