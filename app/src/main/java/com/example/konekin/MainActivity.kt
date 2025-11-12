package com.example.konekin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.konekin.databinding.ActivityMainBinding
import com.example.konekin.model.Data
import com.example.konekin.model.Users
import com.example.konekin.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userList = ArrayList<String>()  // tampilan
    private val dataList = ArrayList<Data>()    // data asli

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tampilkan data saat app dibuka
        getAllEmployees()

        // Tombol CREATE
        binding.btnCreate.setOnClickListener {
            showInputDialog("Create Employee") { name, salary, age, _ ->
                createEmployee(name, salary, age)
            }
        }

        // Tombol UPDATE
        binding.btnUpdate.setOnClickListener {
            showInputDialog("Update Employee (masukkan ID)") { name, salary, age, id ->
                if (id.isNotEmpty()) updateEmployee(id.toInt(), name, salary, age)
                else Toast.makeText(this, "ID harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol DELETE
        binding.btnDelete.setOnClickListener {
            showDeleteDialog()
        }
    }

    // ==========================================================
    // ===================== READ ===============================
    // ==========================================================
    private fun getAllEmployees() {
        val client = ApiClient.getInstance().getAllUsers()
        client.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                if (response.isSuccessful && response.body() != null) {
                    dataList.clear()
                    dataList.addAll(response.body()!!.data)
                    refreshListView()
                } else {
                    Toast.makeText(this@MainActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Koneksi error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ==========================================================
    // ===================== CREATE =============================
    // ==========================================================
    private fun createEmployee(name: String, salary: String, age: String) {
        val call = ApiClient.getInstance().createEmployee(name, salary, age)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                Toast.makeText(this@MainActivity, "Berhasil menambah employee", Toast.LENGTH_SHORT).show()

                // Simulasi: tambahkan ke list lokal
                val newId = (dataList.maxOfOrNull { it.id } ?: 0) + 1
                val newEmployee = Data(newId, name, salary.toInt(), age.toInt(), "")
                dataList.add(newEmployee)
                refreshListView()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal menambah employee", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ==========================================================
    // ===================== UPDATE =============================
    // ==========================================================
    private fun updateEmployee(id: Int, name: String, salary: String, age: String) {
        val call = ApiClient.getInstance().updateEmployee(id, name, salary, age)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                Toast.makeText(this@MainActivity, "Berhasil mengupdate employee", Toast.LENGTH_SHORT).show()

                // Simulasi: perbarui di list lokal
                val index = dataList.indexOfFirst { it.id == id }
                if (index != -1) {
                    val updated = dataList[index].copy(
                        employeeName = name,
                        employeeSalary = salary.toInt(),
                        employeeAge = age.toInt()
                    )
                    dataList[index] = updated
                    refreshListView()
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal update", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ==========================================================
    // ===================== DELETE =============================
    // ==========================================================
    private fun deleteEmployee(id: Int) {
        val call = ApiClient.getInstance().deleteEmployee(id)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(this@MainActivity, "Berhasil menghapus employee", Toast.LENGTH_SHORT).show()

                // Simulasi: hapus dari list lokal
                val index = dataList.indexOfFirst { it.id == id }
                if (index != -1) {
                    dataList.removeAt(index)
                    refreshListView()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal menghapus employee", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ==========================================================
    // ===================== UTILITAS ===========================
    // ==========================================================
    private fun refreshListView() {
        userList.clear()
        for (item in dataList) {
            userList.add("${item.id}. ${item.employeeName} - ${item.employeeAge} yrs - \$${item.employeeSalary}")
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        binding.lvNama.adapter = adapter
    }

    // ==========================================================
    // ===================== DIALOGS ============================
    // ==========================================================
    private fun showInputDialog(title: String, onSubmit: (String, String, String, String) -> Unit) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_input, null)
        val etId = view.findViewById<EditText>(R.id.etId)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etSalary = view.findViewById<EditText>(R.id.etSalary)
        val etAge = view.findViewById<EditText>(R.id.etAge)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setPositiveButton("Submit") { _, _ ->
                onSubmit(
                    etName.text.toString(),
                    etSalary.text.toString(),
                    etAge.text.toString(),
                    etId.text.toString()
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDialog() {
        val etId = EditText(this)
        etId.hint = "Masukkan ID Employee"
        AlertDialog.Builder(this)
            .setTitle("Delete Employee")
            .setView(etId)
            .setPositiveButton("Delete") { _, _ ->
                val id = etId.text.toString()
                if (id.isNotEmpty()) deleteEmployee(id.toInt())
                else Toast.makeText(this, "ID tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
