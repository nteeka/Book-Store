package com.example.BookShop.controller;

import com.example.BookShop.model.Category;
import com.example.BookShop.repos.CateRepo;
import com.example.BookShop.repos.UserRepo;
import com.example.BookShop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired CateRepo cateRepo;

    @Autowired
    CategoryService categoryService;


    @GetMapping("/category_list")
    public String cateView(Model model, @RequestParam(defaultValue = "1") int page) {
        model.addAttribute("title", "Manage Category");
        Page<Category> categories = categoryService.getAll(page);
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("categories", categories);


        model.addAttribute("title", "Manage Category");
        model.addAttribute("size", categories.getSize());
        model.addAttribute("categoryNew", new Category());
        return "Category/categories";
    }


    @PostMapping("/save-category")
    public String save(@ModelAttribute("categoryNew") Category category, Model model, RedirectAttributes redirectAttributes) {

        if(category.getCategoryName() == "" || category.getCategoryName() == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid name!");
            return "redirect:/category/category_list";
        }
        try {
            cateRepo.save(category);
            model.addAttribute("categoryNew", category);
            redirectAttributes.addFlashAttribute("success", "Add successfully!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            model.addAttribute("categoryNew", category);
            redirectAttributes.addFlashAttribute("error",
                    "Error server");
        }
        return "redirect:/category/category_list";
    }

    @RequestMapping(value = "/findById", method = {RequestMethod.PUT, RequestMethod.GET})
    @ResponseBody
    public Optional<Category> findById(String id,Model model) {
        Optional<Category> cate = cateRepo.findById(id);


        return cate;
    }

    @GetMapping("/update-category")
    public String update(Category category, RedirectAttributes redirectAttributes) {

        if(category.getCategoryName() == "" || category.getCategoryName() == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid name!");
            return "redirect:/category/category_list";
        }


        try {
            cateRepo.save(category);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error from server or duplicate name of category, please check again!");
        }
        return "redirect:/category/category_list";
    }

    @RequestMapping(value = "/disable-category", method = {RequestMethod.GET, RequestMethod.PUT})
    public String delete(String id, RedirectAttributes redirectAttributes) {

        Optional<Category> category = cateRepo.findById(id);
        category.get().setDeleted(true);
        cateRepo.save(category.get());
        redirectAttributes.addFlashAttribute("success", "Deleted successfully!");

        return "redirect:/category/category_list";
    }

    @RequestMapping(value = "/enable-category", method = {RequestMethod.PUT, RequestMethod.GET})
    public String enable(String id, RedirectAttributes redirectAttributes) {

        Optional<Category> category = cateRepo.findById(id);
        category.get().setDeleted(false);
        cateRepo.save(category.get());
        redirectAttributes.addFlashAttribute("success", "Enable successfully");

        return "redirect:/category/category_list";
    }
}
