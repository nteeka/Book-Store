package com.example.BookShop.model;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@ToString

@Table(name="User_App")
public class User {

    @Id
    @Column(name = "userID")
    private String userId;

    private String displayName;

    private String phoneNumber;

    private String bio;

    private String image;

    private String address;

    private LocalDate dateCreated;

    private String email;

    private String password;



    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;

    public User() {
        this.userId = RandomStringUtils.randomAlphanumeric(8);
        this.dateCreated = LocalDate.now();
    }
}
