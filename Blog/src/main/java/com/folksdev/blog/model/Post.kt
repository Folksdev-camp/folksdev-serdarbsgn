package com.folksdev.blog.model

import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Post @JvmOverloads constructor(
    @Id
    @Column(name = "post_id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: String? = "",
    val title: String,
    val content: String,
    val date: LocalDateTime = LocalDateTime.now(),

    @field: ElementCollection(fetch = FetchType.EAGER)
    val topicsTypes: List<TopicsType>,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL])
    val comments: Set<Comment> = emptySet(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", referencedColumnName = "blog_id")
    val blog: Blog
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        if (title != other.title) return false
        if (content != other.content) return false
        if (date != other.date) return false
        if (topicsTypes != other.topicsTypes) return false
        if (comments != other.comments) return false
        if (blog != other.blog) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + title.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + topicsTypes.hashCode()
        result = 31 * result + blog.id.hashCode()
        return result
    }
}

enum class TopicsType {
    DEFAULT, COMEDY, GENERAL, DRAMA, NEWS, FANTASY, HORROR, TECH, ECONOMY
}