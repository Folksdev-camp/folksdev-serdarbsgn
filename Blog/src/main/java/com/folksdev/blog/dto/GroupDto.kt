package com.folksdev.blog.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.folksdev.blog.model.GroupsType

data class GroupDto @JvmOverloads constructor(
    val id: String,
    val name: String,
    val description: String,
    val date: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val groupTypes: List<GroupsType>?,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val users: List<UserDto>? = ArrayList(),
    ) {
}