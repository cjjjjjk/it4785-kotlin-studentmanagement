package com.example.studentm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateStudentActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtMSSV: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var btnSave: Button

    private lateinit var studentDao: StudentDao
    private var studentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_student)

        edtName = findViewById(R.id.edtName)
        edtMSSV = findViewById(R.id.edtMSSV)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        btnSave = findViewById(R.id.btnSave)

        studentDao = StudentDatabase.getDatabase(applicationContext).studentDao()

        studentId = intent.getIntExtra("id", -1)
        if (studentId == -1) {
            finish()
            return
        }

        edtName.setText(intent.getStringExtra("name"))
        edtMSSV.setText(intent.getStringExtra("mssv"))
        edtEmail.setText(intent.getStringExtra("email"))
        edtPhone.setText(intent.getStringExtra("phone"))

        btnSave.setOnClickListener {
            val updatedName = edtName.text.toString()
            val updatedMSSV = edtMSSV.text.toString()
            val updatedEmail = edtEmail.text.toString()
            val updatedPhone = edtPhone.text.toString()

            val updatedStudent = Student(
                id = studentId,
                name = updatedName,
                mssv = updatedMSSV,
                email = updatedEmail,
                phone = updatedPhone
            )

            CoroutineScope(Dispatchers.IO).launch {
                studentDao.updateStudent(updatedStudent)

                runOnUiThread {
                    val resultIntent = Intent().apply {
                        putExtra("id", studentId)
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
    }
}
