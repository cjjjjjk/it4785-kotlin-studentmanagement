package com.example.studentm

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Student(
    val id: Int = 0,
    val name: String,
    val mssv: String,
    val email: String,
    val phone: String
)

class StudentAdapter(
    private val students: MutableList<Student>,
    private val onDelete: (Student) -> Unit,
    private val onUpdate: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

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
                        onUpdate(student)
                        true
                    }
                    R.id.menu_delete -> {
                        AlertDialog.Builder(context)
                            .setMessage("Bạn có chắc chắn muốn xóa sinh viên này?")
                            .setPositiveButton("Có") { _, _ ->
                                onDelete(student)
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

    fun updateStudent(updated: Student) {
        val index = students.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            students[index] = updated
            notifyItemChanged(index)
        }
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddNew: Button
    private lateinit var adapter: StudentAdapter
    private lateinit var dbHelper: StudentDatabaseHelper
    private val students = mutableListOf<Student>()

    companion object {
        const val ADD_STUDENT_REQUEST_CODE = 1
        const val UPDATE_STUDENT_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        btnAddNew = findViewById(R.id.btnAddNew)
        dbHelper = StudentDatabaseHelper(this)

        adapter = StudentAdapter(students,
            onDelete = { student ->
                dbHelper.deleteStudent(student.id)
                Log.d("MainActivity", "Deleted student: ${student.id}")
            },
            onUpdate = { student ->
                val intent = Intent(this, UpdateStudentActivity::class.java).apply {
                    putExtra("id", student.id)
                    putExtra("name", student.name)
                    putExtra("mssv", student.mssv)
                    putExtra("email", student.email)
                    putExtra("phone", student.phone)
                }
                startActivityForResult(intent, UPDATE_STUDENT_REQUEST_CODE)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadStudentsFromDatabase()

        btnAddNew.setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivityForResult(intent, ADD_STUDENT_REQUEST_CODE)
        }
    }

    private fun loadStudentsFromDatabase() {
        students.clear()
        students.addAll(dbHelper.getAllStudents())
        adapter.notifyDataSetChanged()
        Log.d("MainActivity", "Loaded ${students.size} students from DB")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val id = data.getIntExtra("id", -1)
            val name = data.getStringExtra("name") ?: return
            val mssv = data.getStringExtra("mssv") ?: return
            val email = data.getStringExtra("email") ?: ""
            val phone = data.getStringExtra("phone") ?: ""
            val student = Student(id, name, mssv, email, phone)

            when (requestCode) {
                ADD_STUDENT_REQUEST_CODE -> adapter.addStudent(student)
                UPDATE_STUDENT_REQUEST_CODE -> adapter.updateStudent(student)
            }
        }
    }
}
