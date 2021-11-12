package com.folksdev.blog.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.folksdev.blog.model.Blog
import com.folksdev.blog.model.TopicsType

data class PostDto @JvmOverloads constructor(

    val title: String,
    val content: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val topicsTypes: List<TopicsType>? = ArrayList(),
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val comments: List<CommentDto>? = ArrayList(),
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val blogTitle: String? = "",
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val userName: String? = ""
    )