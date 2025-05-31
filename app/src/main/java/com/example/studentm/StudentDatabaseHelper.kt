package com.example.studentm

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class StudentDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "student.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "students"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_MSSV = "mssv"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_MSSV TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PHONE TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getAllStudents(): List<Student> {
        val studentList = mutableListOf<Student>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val mssv = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSSV))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))

                studentList.add(Student(id, name, mssv, email, phone))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return studentList
    }

    fun insertStudent(name: String, mssv: String, email: String, phone: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_MSSV, mssv)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
        }

        val result = db.insert(TABLE_NAME, null, values)
        if (result != -1L) {
            Log.d("SQLite", "Insert thành công: $name - $mssv - $email - $phone")
        } else {
            Log.e("SQLite", "Insert thất bại")
        }
        return result
    }

    fun deleteStudent(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun updateStudent(student: Student): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_MSSV, student.mssv)
            put(COLUMN_EMAIL, student.email)
            put(COLUMN_PHONE, student.phone)
        }

        return db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(student.id.toString())
        )
    }
}
