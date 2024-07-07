package com.example.BookShop.service;

import com.example.BookShop.model.Category;
import com.example.BookShop.repos.CateRepo;
import com.example.BookShop.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    CateRepo cateRepo;
    public Page<Category> getAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5); // Zero-based page index
        return this.cateRepo.findAll(pageable);
    }
}
