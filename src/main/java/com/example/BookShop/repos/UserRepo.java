package com.example.BookShop.repos;

import com.example.BookShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {

    @Query("SELECT s FROM User s WHERE s.email = :email AND s.isDeleted = false")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT s FROM User s WHERE s.email = :email AND s.isDeleted = false")
    User getEmail(@Param("email") String email);

    @Query("SELECT s FROM User s WHERE s.resetToken = :resetToken AND s.isDeleted = false")
    Optional<User> findByResetToken(@Param("resetToken") String resetToken);

    @Query("SELECT s FROM User s WHERE s.email = :email AND s.userId != :currentAccountId AND s.isDeleted = false")
    Optional<User> findByEmailAndIdNot(@Param("email") String email, @Param("currentAccountId") String currentAccountId);

}
