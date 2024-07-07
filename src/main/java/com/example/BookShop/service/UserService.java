package com.example.BookShop.service;

import com.example.BookShop.model.User;
import com.example.BookShop.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired UserRepo userRepo;

    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    public User saveUser(User user) {
        return userRepo.save(user);
    }

    //check email in database
    public boolean isEmailTaken(String email) {
        return userRepo.findByEmail(email).isPresent();
    }
    public boolean isEmailTaken(String email,String id) {
        return userRepo.findByEmailAndIdNot(email,id).isPresent();
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
