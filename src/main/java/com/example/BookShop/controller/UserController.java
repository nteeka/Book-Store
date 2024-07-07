package com.example.BookShop.controller;

import com.example.BookShop.helpers.EmailService;

import com.example.BookShop.helpers.PasswordResetTokenUtils;
import com.example.BookShop.model.User;
import com.example.BookShop.repos.UserRepo;
import com.example.BookShop.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    //helpers
    @Autowired
    private EmailService emailService;
    @Autowired private BCryptPasswordEncoder passwordEncoder;


    //user
    @Autowired UserRepo userRepo;
    @Autowired UserService userService;



    @GetMapping("/index")
    public String indexView() {
        return "index";
    }


    @GetMapping("/login")
    public String login(HttpServletRequest  request, HttpServletResponse response) {
        return "Authen/login";
    }

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

        //verify account
        String activationLink = "http://localhost:8080/user/activeUser?userId=" + user.getUserId();
        String emailHtmlContent = "<html><body>"
                + "<h1>Account Activation</h1>"
                + "<p>Please click the following link to activate your account:</p>"
                + "<a href=\"" + activationLink + "\">Activate Account</a>"
                + "</body></html>";
        try {
            emailService.sendEmail(user.getEmail(), "Account Activation", emailHtmlContent);
        } catch (MessagingException e) {
            e.printStackTrace();
            // handle the exception if the email fails to send
        }

        redirectAttributes.addFlashAttribute("currentEmail", user.getEmail());
//      redirectAttributes.addFlashAttribute("currentPassword", password);
        redirectAttributes.addFlashAttribute("notifyRemindVerifyEmail", "We just send you a link to your email.Please check and verify before login !!");

        return "redirect:/user/login";
    }

    //active user - verify email
    @GetMapping("/activeUser")
    public String activateUser(@RequestParam String userId) {
        User user =userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerifyEmail(true);
        userRepo.save(user);

        return "redirect:/user/login";
    }

    @GetMapping("/forgotPass")
    public String showForgotPassForm() {
        return "Authen/forgotPass_Email";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam("email") String email, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> account = userRepo.findByEmail(email);
        if (account.isPresent()) {
            // Generate reset token and save it in the database
            String resetToken = PasswordResetTokenUtils.generateToken();
            account.get().setResetToken(resetToken);
            LocalDateTime expirationTime = LocalDateTime.now().plus(2, ChronoUnit.MINUTES);
            account.get().setResetTokenExpiration(expirationTime);
            userRepo.save(account.get());

            // Send reset password email
            String resetLink = "http://localhost:8080/user/forgotPass_token?token=" + resetToken;

            String emailHtmlContent = "<html><body>"
                    + "<h1>Account Activation</h1>"
                    + "<p>Click the following link to reset your password:</p>"
                    + "<a href=\"" + resetLink + "\">Reset Password</a>"
                    + "</body></html>";
            try {
                emailService.sendEmail(email, "Password Reset", emailHtmlContent);
            } catch (MessagingException e) {
                e.printStackTrace();
                // handle the exception if the email fails to send
            }
        } else {
            redirectAttributes.addFlashAttribute("emailError", "Không tìm thấy tài khoản có email vừa nhập!");
            return "redirect:/user/forgotPass";
        }

        return "redirect:/user/login";
    }

    @GetMapping("/forgotPass_token")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // Check if the token is valid
        Optional<User> account = userRepo.findByResetToken(token);

        if (account.isPresent() && !account.get().isResetTokenExpired(account.get().getResetTokenExpiration())) {
            model.addAttribute("resetToken", token);
            return "Authen/forgotPass_Token";
        } else {
//          return "invalidTokenPage";
            return "redirect:/user/loginFail";
        }
    }


    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword, Model model) {
        Optional<User> account = userRepo.findByResetToken(token);

        if (newPassword.length() < 6) {
            model.addAttribute("passwordError", "Password must be at least 6 characters long.");
            model.addAttribute("resetToken", token);
            return "Authen/forgotPass_Token";
        }
        if (!userService.containsUppercaseAndLowercase(newPassword)) {
            model.addAttribute("passwordError",
                    "Password must contain at least one uppercase letter and one lowercase letter.");
            model.addAttribute("resetToken", token);
            return "Authen/forgotPass_Token";
        }
        if (!confirmPassword.equals(newPassword)) {
            model.addAttribute("resetToken", token);
            model.addAttribute("passwordError", "New pass and confirm pass is not match");
            return "Authen/forgotPass_Token";
        }

        if (account != null) {
            // Update the password and reset the token
            String hashedPassword = passwordEncoder.encode(newPassword);
            account.get().setPassword(hashedPassword);
            account.get().setResetToken(null);
            account.get().setResetTokenExpiration(null);
            userRepo.save(account.get());
            model.addAttribute("passwordReset", "Thay đổi mật khẩu thành công, vui lòng đăng nhập lại!");
        } else {
            model.addAttribute("passwordReset", "Thay đổi mật khẩu không thành công!");
        }
        return "Authen/login";
    }


    @GetMapping("/edit/{id}")
    public String editAccountForm(@PathVariable String id, Model m, HttpServletRequest request) {

        Optional<User> acc = userRepo.findById(id);
        m.addAttribute("account",acc.get());
        return "/User/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateAccount(@PathVariable("id") String id,
                                @RequestParam("displayName") String displayName,
                                @RequestParam("email") String Email,
                                @RequestParam("bio") String bio,
                                @RequestParam("phoneNumber") String phoneNumber,
                                Model model) {

        Optional<User> existingAccount = userRepo.findById(id);
//        if (!existingAccount.isPresent()) {
//            return "redirect:/StudentView/listStudent"; // error, change after	    ???
//        }
        User account = existingAccount.get();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Email.matches(emailRegex) || userService.isEmailTaken(Email,id)) {
            Optional<User> account1 = userService.getUserById(id);
            model.addAttribute("emailError", "Email is already taken or Email is invalid. Please choose a different one.");
            String imageUrl = "/image/getImage/" + account.getImage();
            model.addAttribute("account",account1);
            model.addAttribute("imageUrl", imageUrl);
            return "/User/edit";
        }
        account.setEmail(Email);
        account.setBio(bio);
        account.setDisplayName(displayName);

        account.setPhoneNumber(phoneNumber);

        userRepo.save(account);
        return "redirect:/user/edit/" + existingAccount.get().getUserId();
    }
    @PostMapping("/saveImage")
    public String saveImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

    	HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
	    if (loggedInUser == null) {
	        return "/user/login";
	    }
	    Optional<User> account = userRepo.findById(loggedInUser.getUserId()); // Thay userId bằng userId của người dùng

	    Path path = Paths.get("uploads/");
	    try {
	        InputStream inputStream = file.getInputStream();
	        // Tạo tên file mới với định dạng account_img_id
	        String newFileName = "account_img_" + account.get().getUserId();
	        Files.copy(inputStream, path.resolve(newFileName), StandardCopyOption.REPLACE_EXISTING);
	        account.get().setImage(newFileName);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		userRepo.save(account.get());
		return "redirect:/user/edit" + loggedInUser.getUserId();
    }


    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

        return "redirect:/user/login";
    }

    @GetMapping("/loginsuccess")
    public String loginSuccess(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        return "loginsuccess";
    }



    @GetMapping("/loginFail")
    public String loginFail(RedirectAttributes redirectAttributes,HttpServletRequest request, Model model) {

        redirectAttributes.addFlashAttribute("loginFail", "Email hoặc mật khẩu không đúng, vui lòng đăng nhập lại!");
        return "redirect:/user/login";
    }




    //Lưu lại url nếu người dùng chưa đăng nhập
    @GetMapping("/testUrl")
    public String test(HttpServletRequest request) {
        //Lưu lại url nếu người dùng chưa đăng nhập

        return "/index";
    }






}
