package com.folksdev.blog.model

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
data class Blog @JvmOverloads constructor(
    @Id
    @Column(name = "blog_id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: String? = "",
    val title: String,
    val description: String,
    val content: String,
    val date: String,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    val user: User,

    @OneToMany(mappedBy = "blog", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val posts: Set<Post>

){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Blog

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (content != other.content) return false
        if (date != other.date) return false
        if (user != other.user) return false
        if (posts != other.posts) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + user.id.hashCode()
        return result
    }
}
