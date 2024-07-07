package com.example.BookShop.config;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        //HttpSession session = request.getSession();
        //session.setAttribute("errorLoginWithoutVerifyEmail", exception.getMessage());
        response.sendRedirect("/user/loginFail");

    }
}

