package com.example.BookShop.model;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Table(name="Category",uniqueConstraints = @UniqueConstraint(columnNames = "categoryName"))
public class Category {
    @Id
    @Column(name = "categoryId",unique = true)
    private String categoryId;

    @Column(name = "categoryName", unique = true)
    private String categoryName;

    private LocalDate dateCreated;

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;

    public Category() {
        this.categoryId = RandomStringUtils.randomAlphanumeric(8);
        this.dateCreated = LocalDate.now();
    }
}
