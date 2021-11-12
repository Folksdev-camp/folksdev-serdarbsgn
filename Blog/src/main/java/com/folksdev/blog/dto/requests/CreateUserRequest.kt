package com.folksdev.blog.dto.requests
import com.folksdev.blog.model.Gender
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


data class CreateUserRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val surname: String,
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val dateOfBirth: String,
    @field:NotNull
    val gender: Gender,
    val groupsIds: List<String>? = ArrayList(),
    val commentsIds: List<String>? =ArrayList()
)