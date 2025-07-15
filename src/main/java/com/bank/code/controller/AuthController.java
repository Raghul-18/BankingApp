package com.bank.code.controller;

import com.bank.code.entity.User;
import com.bank.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired private UserService userService;

    @GetMapping("/") public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) return "redirect:/dashboard";
        return "redirect:/login";
    }
    @GetMapping("/login") public String login(Model model, @RequestParam(value="error", required=false) String error, @RequestParam(value="logout", required=false) String logout) {
        if (error != null) model.addAttribute("error", "Invalid username or password!");
        if (logout != null) model.addAttribute("message", "You have been logged out successfully!");
        return "login";
    }
    @GetMapping("/register") public String register(Model model) { model.addAttribute("user", new User()); return "register"; }
    @PostMapping("/register") public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) return "register";
        if (userService.existsByUsername(user.getUsername())) { model.addAttribute("error", "Username already exists!"); return "register"; }
        if (userService.existsByEmail(user.getEmail())) { model.addAttribute("error", "Email already exists!"); return "register"; }
        try {
            userService.registerUser(user);
            ra.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Please try again.");
            return "register";
        }
    }

    @GetMapping("/profile") public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        model.addAttribute("user", user);
        return "profile";
    }
}

