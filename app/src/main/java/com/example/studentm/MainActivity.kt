package com.example.studentm

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.PopupMenu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

data class Student(
    val name: String,
    val mssv: String,
    val email: String,
    val phone: String
)

class StudentAdapter(private val students: MutableList<Student>) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtMSSV: TextView = itemView.findViewById(R.id.txtMSSV)
        val btnMenu: View = itemView.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.txtName.text = student.name
        holder.txtMSSV.text = "MSSV: ${student.mssv}"

        holder.btnMenu.setOnClickListener { view ->
            val context = view.context
            val popup = PopupMenu(context, view)
            popup.menuInflater.inflate(R.menu.student_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_update -> {
                        val intent = Intent(context, UpdateStudentActivity::class.java).apply {
                            putExtra("name", student.name)
                            putExtra("mssv", student.mssv)
                            putExtra("email", student.email)
                            putExtra("phone", student.phone)
                        }
                        (context as MainActivity).startActivityForResult(intent, MainActivity.UPDATE_STUDENT_REQUEST_CODE)
                        true
                    }
                    R.id.menu_delete -> {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Bạn có chắc chắn muốn xóa sinh viên này?")
                            .setPositiveButton("Có") { _, _ ->
                                students.removeAt(holder.adapterPosition)
                                notifyItemRemoved(holder.adapterPosition)
                            }
                            .setNegativeButton("Không", null)
                            .show()
                        true
                    }
                    R.id.menu_call -> {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${student.phone}")
                        }
                        context.startActivity(intent)
                        true
                    }
                    R.id.menu_email -> {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${student.email}")
                        }
                        context.startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = students.size

    fun addStudent(student: Student) {
        students.add(0, student)
        notifyItemInserted(0)
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddNew: Button
    private val students = mutableListOf<Student>()
    private lateinit var adapter: StudentAdapter

    companion object {
        const val ADD_STUDENT_REQUEST_CODE = 1
        const val UPDATE_STUDENT_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        btnAddNew = findViewById(R.id.btnAddNew)

        adapter = StudentAdapter(students)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnAddNew.setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivityForResult(intent, ADD_STUDENT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_STUDENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val name = data.getStringExtra("name") ?: return
            val mssv = data.getStringExtra("mssv") ?: return
            val email = data.getStringExtra("email") ?: ""
            val phone = data.getStringExtra("phone") ?: ""

            adapter.addStudent(Student(name, mssv, email, phone))
        }

        if (requestCode == UPDATE_STUDENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val updatedName = data.getStringExtra("name") ?: return
            val updatedMSSV = data.getStringExtra("mssv") ?: return
            val updatedEmail = data.getStringExtra("email") ?: ""
            val updatedPhone = data.getStringExtra("phone") ?: ""

            val updatedStudent = Student(updatedName, updatedMSSV, updatedEmail, updatedPhone)
            students[students.indexOfFirst { it.mssv == updatedMSSV }] = updatedStudent
            adapter.notifyDataSetChanged()
        }
    }
}
