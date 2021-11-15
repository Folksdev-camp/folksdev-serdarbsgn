package com.folksdev.blog.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.folksdev.blog.model.Gender
import java.time.LocalDate

data class UserDto @JvmOverloads constructor(
    var username: String,
    var dateOfBirth: LocalDate,
    var gender: Gender,
    var email: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var groups: List<GroupDto>? = ArrayList(),
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var comments: List<CommentDto>? = ArrayList()
)
