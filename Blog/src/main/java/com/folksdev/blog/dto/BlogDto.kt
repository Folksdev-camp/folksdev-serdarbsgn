package com.folksdev.blog.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate
import kotlin.collections.ArrayList

data class BlogDto @JvmOverloads constructor(

    val title: String,
    val description: String,
    val content: String,
    val date: LocalDate,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val user: UserDto? = null,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val posts: List<PostDto>? = ArrayList()
    )