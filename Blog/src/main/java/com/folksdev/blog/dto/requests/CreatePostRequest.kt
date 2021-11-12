package com.folksdev.blog.dto.requests

import com.folksdev.blog.model.TopicsType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreatePostRequest(
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val content: String,
    @field:NotNull
    val topicsTypes: List<TopicsType>?
)