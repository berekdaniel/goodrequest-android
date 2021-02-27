package com.example.goodrequest.rest

import com.example.goodrequest.model.response.UserListResponse
import com.example.goodrequest.model.response.UserResponse
import retrofit2.http.*
import rx.Observable


interface ApiService {
    @GET("users")
    fun getUsers(@Query("page") page: Int, @Query("per_page") perPage: Int): Observable<UserListResponse>

    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: Int): Observable<UserResponse>
}