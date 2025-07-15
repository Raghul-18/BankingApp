package com.bank.code.controller;

import com.bank.code.entity.User;
import com.bank.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;

    @Autowired
    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal User loggedInUser, Model model) {
        model.addAttribute("user", loggedInUser);

        // Check for ROLE_ADMIN and ROLE_MANAGER
        boolean isAdmin = loggedInUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isManager = loggedInUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isManager", isManager);

        userService.updateLastLogin(loggedInUser.getUsername());

        if (isAdmin) {
            return "admin_dashboard";
        }
        return "user_dashboard";
    }
}
