package com.example.studentm

import androidx.room.*

@Dao
interface StudentDao {

    @Query("SELECT * FROM students")
    fun getAllStudents(): List<Student>

    @Query("""
        SELECT * FROM students
        WHERE name LIKE '%' || :keyword || '%'
           OR mssv LIKE '%' || :keyword || '%'
           OR email LIKE '%' || :keyword || '%'
           OR phone LIKE '%' || :keyword || '%'
        """)
    suspend fun searchStudents(keyword: String): List<Student>

    @Insert
    fun insertStudent(student: Student): Long

    @Delete
    fun deleteStudent(student: Student): Int

    @Update
    fun updateStudent(student: Student): Int
}
