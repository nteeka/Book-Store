package com.example.BookShop.service;

import com.example.BookShop.model.User;
import com.example.BookShop.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired UserRepo userRepo;


    public User saveUser(User user) {
        return userRepo.save(user);
    }

    //check email in database
    public boolean isEmailTaken(String email) {
        return userRepo.findByEmail(email).isPresent();
    }
    //check mật khẩu có ký tự hoa
    public boolean containsUppercaseAndLowercase(String str) {
        boolean hasUppercase = false;
        boolean hasLowercase = false;

        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            }
        }

        return hasUppercase && hasLowercase;
    }

}
