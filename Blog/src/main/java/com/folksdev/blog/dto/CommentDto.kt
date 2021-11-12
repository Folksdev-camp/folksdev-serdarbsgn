package com.folksdev.blog.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class CommentDto @JvmOverloads constructor(
    val body: String,
    val date: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val postTitle: String? = "",
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val username: String? = "",
)
