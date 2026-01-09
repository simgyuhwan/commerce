package com.flowcommerce.core.storage;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityStatus status = EntityStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void active(){
        this.status = EntityStatus.ACTIVE;
    }

    public Boolean isActive() {
        return status == EntityStatus.ACTIVE;
    }

    public void delete(){
        this.status = EntityStatus.DELETED;
    }

    public Boolean isDeleted() {
        return status == EntityStatus.DELETED;
    }

}
