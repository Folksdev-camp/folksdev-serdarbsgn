package com.folksdev.blog.model

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.OnDelete
import javax.persistence.*

@Entity
@Table(name = "blog_group")
data class Group @JvmOverloads constructor(
    @Id
    @Column(name = "group_id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: String? = "",
    val name: String,
    val description: String,
    val date: String,

    @field: ElementCollection(fetch = FetchType.EAGER)
    val groupsTypes: List<GroupsType>,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_groups",
        joinColumns = [JoinColumn(name = "group_id", referencedColumnName = "group_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "user_id")]
    )
    val users: Set<User>? = HashSet()

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (date != other.date) return false
        if (groupsTypes != other.groupsTypes) return false
        if (users != other.users) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + groupsTypes.hashCode()
        return result
    }
}

enum class GroupsType {
    DEFAULT, COMEDY, GENERAL, DRAMA, NEWS, FANTASY, HORROR
}