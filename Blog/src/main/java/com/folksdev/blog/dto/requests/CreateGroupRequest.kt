package com.folksdev.blog.dto.requests

import com.folksdev.blog.model.GroupsType
import javax.validation.constraints.NotBlank

data class CreateGroupRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val description: String,
    @field:NotBlank
    val date: String,
    val groupsTypes: List<GroupsType>? = listOf(GroupsType.DEFAULT)
)
