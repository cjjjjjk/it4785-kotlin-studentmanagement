package com.example.studentm

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

private lateinit var dbHelper: StudentDatabaseHelper

class AddStudentActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtMSSV: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_student_menu)

        initViews()
        dbHelper = StudentDatabaseHelper(this)

        sharedPref = getSharedPreferences("student_input", MODE_PRIVATE)
        restoreInputData()

        btnSave.setOnClickListener { saveStudentData() }
        btnCancel.setOnClickListener { cancelAndSaveInputData() }
    }

    private fun initViews() {
        edtName = findViewById(R.id.edtName)
        edtMSSV = findViewById(R.id.edtMSSV)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun restoreInputData() {
        edtName.setText(sharedPref.getString("name", ""))
        edtMSSV.setText(sharedPref.getString("mssv", ""))
        edtEmail.setText(sharedPref.getString("email", ""))
        edtPhone.setText(sharedPref.getString("phone", ""))
    }

    private fun saveInputDataToLocal() {
        with(sharedPref.edit()) {
            putString("name", edtName.text.toString())
            putString("mssv", edtMSSV.text.toString())
            putString("email", edtEmail.text.toString())
            putString("phone", edtPhone.text.toString())
            apply()
        }
    }

    private fun clearLocalData() {
        sharedPref.edit().clear().apply()
    }

    private fun saveStudentData() {
        val name = edtName.text.toString()
        val mssv = edtMSSV.text.toString()
        val email = edtEmail.text.toString()
        val phone = edtPhone.text.toString()

        val rowId = dbHelper.insertStudent(name, mssv, email, phone)

        if (rowId != -1L) {
            clearLocalData()

            val resultIntent = Intent().apply {
                putExtra("name", name)
                putExtra("mssv", mssv)
                putExtra("email", email)
                putExtra("phone", phone)
            }

            setResult(RESULT_OK, resultIntent)
        }
        finish()
    }

    private fun cancelAndSaveInputData() {
        saveInputDataToLocal()
        finish()
    }
}
