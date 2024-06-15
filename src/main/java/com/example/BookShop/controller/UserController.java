package com.example.BookShop.controller;

import com.example.BookShop.model.User;
import com.example.BookShop.repos.UserRepo;
import com.example.BookShop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired private BCryptPasswordEncoder passwordEncoder;

    //user
    @Autowired UserRepo userRepo;
    @Autowired UserService userService;




    @GetMapping("/login")
    public String login() {  return "Authen/login";}

    @GetMapping("/register")
    public String registerView() {
        return "Authen/register";
    }

    @GetMapping("/check")
    public String check() {
        System.out.println(userRepo.findAll());
        return "Authen/register";
    }


    @PostMapping("/register")
    public String addUser1(@RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("confirm-password") String confirmPassword,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {

        //check email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        //trường hợp email không hợp lệ
        if(!email.matches(emailRegex))
        {
            redirectAttributes.addFlashAttribute("emailError", "Email is invalid. Please choose a different one.");
            return "redirect:/user/register";
        }
        // trường hợp bị trùng lắp email
        if (userService.isEmailTaken(email)) {
            redirectAttributes.addFlashAttribute("emailError", "Email is already taken. Please choose a different one.");
            return "redirect:/user/register";
        }
        redirectAttributes.addFlashAttribute("emailValid", email);

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("passwordError", "Password must be at least 6 characters long.");
            return "redirect:/user/register";
        }
        if (!userService.containsUppercaseAndLowercase(password)) {
            redirectAttributes.addFlashAttribute("passwordError", "Password must contain at least one uppercase letter and one lowercase letter.");
            return "redirect:/user/register";
        }

        if (!confirmPassword.equals(password)) {
            redirectAttributes.addFlashAttribute("passwordError", "New pass and confirm pass is not match");
            return "redirect:/user/register";
        }
        User user = new User();

        user.setEmail(email);

        //mã hóa mật khẩu
        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);

        user.setDisplayName(email); // displayName default

        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("currentEmail", user.getEmail());
        redirectAttributes.addFlashAttribute("currentPassword", password);
        return "redirect:/user/login";
    }




    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Xóa thông tin xác thực hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        // Xóa cookie JSESSIONID
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // Chuyển hướng người dùng đến trang đăng nhập
        return "redirect:/user/login"; // hoặc trang bạn muốn chuyển hướng sau khi logout
    }

    @GetMapping("/loginsuccess")
    public String loginSuccess() {
        return "loginsuccess";
    }



    @GetMapping("/loginFail")
    public String loginFail(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("loginFail", "Email hoặc mật khẩu không đúng, vui lòng đăng nhập lại!");
        return "redirect:/user/login";
    }




    //Lưu lại url nếu người dùng chưa đăng nhập
    @GetMapping("/testUrl")
    public String test(HttpServletRequest request) {
        //Lưu lại url nếu người dùng chưa đăng nhập
        if(!SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
        {
            HttpSession session = request.getSession();
            session.setAttribute("url_prior_login", "/user/testUrl");
        }
        return "/index";
    }



}
