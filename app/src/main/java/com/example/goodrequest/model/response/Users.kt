package com.example.goodrequest.model.response

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("email") val email: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("avatar") val avatar: String? = null)
{
    val fullname : String
    get() {
        var fullName = ""
        if(!firstName.isNullOrEmpty())
            fullName = firstName
        if(!lastName.isNullOrEmpty()) {
            if(fullName.isNotEmpty())
                fullName += " $lastName"
            else
                fullName = lastName
        }
        return fullName
    }
}
data class UserResponse(@SerializedName("data") val user: UserData? = null)

data class UserListResponse(
    @SerializedName("page") val page: Int? = null,
    @SerializedName("per_page") val batch: Int? = null,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("total_pages") val totalPages: Int? = null,
    @SerializedName("data") val users: List<UserData>? = null
)