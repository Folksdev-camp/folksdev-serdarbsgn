package com.folksdev.blog.dto.requests

import com.folksdev.blog.model.TopicsType
import javax.validation.constraints.NotBlank

data class CreatePostRequest(
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val content: String,
    val topicsTypes: List<TopicsType> = listOf(TopicsType.DEFAULT)
)