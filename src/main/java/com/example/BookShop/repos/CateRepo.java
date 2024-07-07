package com.example.BookShop.repos;


import com.example.BookShop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface CateRepo extends JpaRepository<Category, String> {
    Page<Category> findAll(Pageable pageable);
}
