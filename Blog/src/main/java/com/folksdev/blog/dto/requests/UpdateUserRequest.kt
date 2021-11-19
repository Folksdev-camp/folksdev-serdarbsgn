package com.folksdev.blog.dto.requests

import com.folksdev.blog.model.Gender
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UpdateUserRequest(
    @field:NotBlank
    val username: String,
    @field:Email
    val email: String,
    @field:NotBlank
    val dateOfBirth: String,
    @field:NotNull
    val gender: Gender,
)