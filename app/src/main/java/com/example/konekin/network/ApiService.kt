package com.example.konekin.network

import com.example.konekin.model.Data
import com.example.konekin.model.Users
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // GET semua employee
    @GET("employees")
    fun getAllUsers(): Call<Users>

    // POST (Create new employee)
    @FormUrlEncoded
    @POST("create")
    fun createEmployee(
        @Field("name") name: String,
        @Field("salary") salary: String,
        @Field("age") age: String
    ): Call<Data>

    // PATCH (Update employee)
    @FormUrlEncoded
    @PATCH("update/{id}")
    fun updateEmployee(
        @Path("id") id: Int,
        @Field("name") name: String,
        @Field("salary") salary: String,
        @Field("age") age: String
    ): Call<Data>

    // DELETE (Hapus employee)
    @DELETE("delete/{id}")
    fun deleteEmployee(
        @Path("id") id: Int
    ): Call<Void>
}
