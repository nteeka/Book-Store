package com.example.BookShop.config;

import com.example.BookShop.repos.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import org.springframework.security.web.DefaultRedirectStrategy;


public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {


        // Retrieve the saved redirect URL from the session
        HttpSession session = request.getSession();
        String redirectUrl = (String) session.getAttribute("redirectUrl");

        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            // Clear the session attribute
            session.removeAttribute("redirectUrl");
            // Redirect the user to the saved redirect URL
            response.sendRedirect(redirectUrl);
        } else {
            // If no redirect URL found in session, redirect to default URL
            response.sendRedirect("/user/index"); // or any other default URL
        }
    }


}
