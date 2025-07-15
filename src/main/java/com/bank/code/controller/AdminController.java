package com.bank.code.controller;

import com.bank.code.entity.User;
import com.bank.code.service.AccountService;
import com.bank.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("totalUsers", userService.getTotalUsersCount());
        model.addAttribute("totalAccounts", accountService.getTotalAccounts());
        model.addAttribute("totalBalance", accountService.getTotalBalance());
        model.addAttribute("totalTransactions", 0); // implement later
        model.addAttribute("pendingApprovals", 0);   // implement later
        model.addAttribute("securityAlerts", 0);     // implement later
        return "admin_dashboard";
    }

    @GetMapping("/admin/account-management")
    public String manageAccounts(Model model) {
        model.addAttribute("accounts", accountService.getAllAccounts());
        model.addAttribute("totalAccounts", accountService.getTotalAccounts());
        model.addAttribute("activeAccounts", accountService.getActiveAccountsCount());
        model.addAttribute("suspendedAccounts", accountService.getSuspendedAccountsCount());
        model.addAttribute("totalBalance", accountService.getTotalBalance());
        return "account_management_template";
    }

    @GetMapping("/admin/users")
    public String manageUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Create Pageable object (0-based indexing)
        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated users WITH accounts
        Page<User> userPage = userService.getAllUsers(pageable);

        // Map users to structure including account counts
        List<Map<String, Object>> userData = userPage.getContent().stream().map(user -> {
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            String rolesString = user.getRoles() != null
                    ? user.getRoles().stream()
                    .map(role -> role.getRoleName())
                    .collect(Collectors.joining(", "))
                    : "";
            data.put("rolesString", rolesString);
            data.put("accountCount", user.getAccounts() != null ? user.getAccounts().size() : 0);
            return data;
        }).collect(Collectors.toList());

        // Add attributes to the model
        model.addAttribute("users", userData);
        model.addAttribute("totalUsers", userPage.getTotalElements());
        model.addAttribute("currentPage", userPage.getNumber() + 1); // 1-based for template
        model.addAttribute("pageSize", userPage.getSize());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("activeUsers", userService.getActiveUsersCount());
        model.addAttribute("suspendedUsers", userService.getSuspendedUsersCount());

        return "user_management_template";
    }

    @PostMapping("/admin/users/{id}/promote")
    public String promoteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.addRoleToUser(id, "ADMIN");
            ra.addFlashAttribute("message", "User promoted to Admin successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to promote user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
