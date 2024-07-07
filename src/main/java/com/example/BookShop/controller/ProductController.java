package com.example.BookShop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Product")
public class ProductController {

    @GetMapping("/list")
    public String listProduct() {
        return "Product/listProduct";
    }

    @GetMapping("/detail")
    public String detail() {
        return "Product/detail";
    }

}
