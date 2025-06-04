package com.example.studentm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val name: String? = null,

    val mssv: String? = null,

    val email: String? = null,

    val phone: String? = null
)

