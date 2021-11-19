package com.folksdev.blog.dto.requests
import javax.validation.constraints.NotBlank

data class CreateBlogRequest(
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val description: String,
    val content: String,
)