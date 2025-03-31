package com.example.studentm

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter
import android.widget.LinearLayout

data class Student(val name: String, val mssv: String)

class StudentAdapter(private val context: Context, private val students: MutableList<Student>) : BaseAdapter() {
    override fun getCount(): Int = students.size
    override fun getItem(position: Int): Any = students[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)

            addView(TextView(context).apply {
                id = View.generateViewId()
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })

            addView(TextView(context).apply {
                id = View.generateViewId()
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })

            addView(Button(context).apply {
                id = View.generateViewId()
                text = "Xóa"
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            })
        }

        val txtName: TextView = (view as LinearLayout).getChildAt(0) as TextView
        val txtMSSV: TextView = view.getChildAt(1) as TextView
        val btnDelete: Button = view.getChildAt(2) as Button


        val student = students[position]
        txtName.text = student.name
        txtMSSV.text = student.mssv

        btnDelete.setOnClickListener {
            students.removeAt(position)
            notifyDataSetChanged()
        }

        return view
    }

    fun addStudent(student: Student) {
        students.add(student)
        notifyDataSetChanged()
    }
}


class MainActivity : AppCompatActivity() {
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var edtName: EditText
    private lateinit var edtMSSV: EditText
    private lateinit var btnAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtName = findViewById(R.id.edtName)
        edtMSSV = findViewById(R.id.edtMSSV)
        btnAdd = findViewById(R.id.btnAdd)
        val listView: ListView = findViewById(R.id.listView)

        studentAdapter = StudentAdapter(this, mutableListOf())
        listView.adapter = studentAdapter

        btnAdd.setOnClickListener {
            val name = edtName.text.toString().trim()
            val mssv = edtMSSV.text.toString().trim()

            if (name.isNotEmpty() && mssv.isNotEmpty()) {
                studentAdapter.addStudent(Student(name, mssv))
                edtName.text.clear()
                edtMSSV.text.clear()
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
