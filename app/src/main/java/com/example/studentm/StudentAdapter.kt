package com.example.studentm

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog

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
                                val index = holder.adapterPosition
                                if (index != RecyclerView.NO_POSITION) {
                                    students.removeAt(index)
                                    notifyItemRemoved(index)
                                }
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
