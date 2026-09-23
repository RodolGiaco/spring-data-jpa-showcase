package com.rodo_pizzeria.persistence.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * Base class for audited entities. Its columns are inherited by every subclass table,
 * and Spring Data's {@code AuditingEntityListener} fills them on insert and update
 * (enabled by {@code @EnableJpaAuditing}).
 */
@MappedSuperclass
public class AuditableEntity {

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;

}
