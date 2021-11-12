package com.folksdev.blog.dto.requests

import javax.validation.constraints.NotBlank

data class CreateBlogRequest(

    @NotBlank
    val title: String,
    @NotBlank
    val description: String,
    val content: String,
    val date: String,

)