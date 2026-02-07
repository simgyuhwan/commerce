package com.flowcommerce.core.storage

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EntityStatus = EntityStatus.ACTIVE
        protected set

    @CreationTimestamp
    var createdAt: LocalDateTime? = null
        protected set

    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null
        protected set

    fun active() {
        this.status = EntityStatus.ACTIVE
    }

    val isActive: Boolean
        get() = status == EntityStatus.ACTIVE

    fun delete() {
        this.status = EntityStatus.DELETED
    }

    val isDeleted: Boolean
        get() = status == EntityStatus.DELETED
}
