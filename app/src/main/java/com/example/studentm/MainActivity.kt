package com.example.studentm

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddNew: Button
    private lateinit var adapter: StudentAdapter
    private val students = mutableListOf<Student>()
    private lateinit var searchView: SearchView

    private lateinit var studentDao: StudentDao
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        const val ADD_STUDENT_REQUEST_CODE = 1
        const val UPDATE_STUDENT_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        btnAddNew = findViewById(R.id.btnAddNew)
        searchView = findViewById(R.id.searchView) // cần thêm SearchView trong layout

        studentDao = StudentDatabase.getDatabase(applicationContext).studentDao()

        adapter = StudentAdapter(students,
            onDelete = { student ->
                mainScope.launch(Dispatchers.IO) {
                    studentDao.deleteStudent(student)
                    withContext(Dispatchers.Main) {
                        val index = students.indexOfFirst { it.id == student.id }
                        if (index != -1) {
                            students.removeAt(index)
                            adapter.notifyItemRemoved(index)
                        }
                        Log.d("MainActivity", "Deleted student: ${student.id}")
                    }
                }
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

        // --- Xử lý SearchView ---
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchStudents(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    loadStudentsFromDatabase()
                } else {
                    searchStudents(newText)
                }
                return true
            }
        })
    }

    private fun loadStudentsFromDatabase() {
        mainScope.launch {
            val list = withContext(Dispatchers.IO) {
                studentDao.getAllStudents()
            }
            students.clear()
            students.addAll(list)
            adapter.notifyDataSetChanged()
            Log.d("MainActivity", "Loaded ${students.size} students from DB")
        }
    }

    private fun searchStudents(keyword: String) {
        mainScope.launch {
            val results = withContext(Dispatchers.IO) {
                studentDao.searchStudents(keyword)
            }
            students.clear()
            students.addAll(results)
            adapter.notifyDataSetChanged()
            Log.d("MainActivity", "Search result count: ${results.size} for keyword: $keyword")
        }
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
                ADD_STUDENT_REQUEST_CODE -> {
                    mainScope.launch(Dispatchers.IO) {
                        val newId = studentDao.insertStudent(student)
                        if (newId != -1L) {
                            val studentWithId = student.copy(id = newId.toInt())
                            withContext(Dispatchers.Main) {
                                adapter.addStudent(studentWithId)
                            }
                        }
                    }
                }
                UPDATE_STUDENT_REQUEST_CODE -> {
                    mainScope.launch(Dispatchers.IO) {
                        studentDao.updateStudent(student)
                        withContext(Dispatchers.Main) {
                            adapter.updateStudent(student)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}
