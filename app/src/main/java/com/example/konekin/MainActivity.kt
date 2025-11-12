package com.example.konekin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load semua data saat aplikasi dibuka
        getAllEmployees()

        // Tombol CREATE
        binding.btnCreate.setOnClickListener {
            val name = binding.etName.text.toString()
            val salary = binding.etSalary.text.toString()
            val age = binding.etAge.text.toString()

            if (name.isNotEmpty() && salary.isNotEmpty() && age.isNotEmpty()) {
                createEmployee(name, salary, age)
            } else {
                Toast.makeText(this, "Isi semua field terlebih dahulu!", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol UPDATE
        binding.btnUpdate.setOnClickListener {
            val id = binding.etId.text.toString()
            val name = binding.etName.text.toString()
            val salary = binding.etSalary.text.toString()
            val age = binding.etAge.text.toString()

            if (id.isNotEmpty() && name.isNotEmpty() && salary.isNotEmpty() && age.isNotEmpty()) {
                updateEmployee(id.toInt(), name, salary, age)
            } else {
                Toast.makeText(this, "Isi ID dan field lain untuk update!", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol DELETE
        binding.btnDelete.setOnClickListener {
            val id = binding.etId.text.toString()

            if (id.isNotEmpty()) {
                deleteEmployee(id.toInt())
            } else {
                Toast.makeText(this, "Isi ID terlebih dahulu untuk delete!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAllEmployees() {
        val client = ApiClient.getInstance()
        val response = client.getAllUsers()
        val userList = ArrayList<String>()

        response.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                if (response.isSuccessful && response.body() != null) {
                    for (i in response.body()!!.data) {
                        userList.add("${i.id}. ${i.employeeName} - ${i.employeeAge} yrs - \$${i.employeeSalary}")
                    }
                    val listAdapter = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_list_item_1,
                        userList
                    )
                    binding.lvNama.adapter = listAdapter
                } else {
                    Toast.makeText(this@MainActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Koneksi error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun createEmployee(name: String, salary: String, age: String) {
        val client = ApiClient.getInstance()
        val call = client.createEmployee(name, salary, age)

        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "✅ Employee created successfully", Toast.LENGTH_SHORT).show()
                    getAllEmployees()
                    clearFields()
                } else {
                    Toast.makeText(this@MainActivity, "❌ Failed to create employee", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateEmployee(id: Int, name: String, salary: String, age: String) {
        val client = ApiClient.getInstance()
        val call = client.updateEmployee(id, name, salary, age)

        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "✅ Employee updated successfully", Toast.LENGTH_SHORT).show()
                    getAllEmployees()
                    clearFields()
                } else {
                    Toast.makeText(this@MainActivity, "❌ Failed to update employee", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteEmployee(id: Int) {
        val client = ApiClient.getInstance()
        val call = client.deleteEmployee(id)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "✅ Employee deleted successfully", Toast.LENGTH_SHORT).show()
                    getAllEmployees()
                    clearFields()
                } else {
                    Toast.makeText(this@MainActivity, "❌ Failed to delete employee", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun clearFields() {
        binding.etId.text.clear()
        binding.etName.text.clear()
        binding.etSalary.text.clear()
        binding.etAge.text.clear()
    }
}
