package com.folksdev.blog.dto.requests

import javax.validation.constraints.NotBlank


data class CreateCommentRequest (
    @field: NotBlank
    val body: String
    )