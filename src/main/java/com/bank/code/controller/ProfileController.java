
package com.bank.code.controller;

import com.bank.code.entity.User;
import com.bank.code.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // View Profile Page
    @GetMapping
    public String viewProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile"; // This is your profile.html template
    }

    // Show Edit Profile Form
    @GetMapping("/edit")
    public String editProfileForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "edit-profile"; // Create this template or reuse a fragment
    }

    // Submit Edit Profile Form
    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute("user") @Valid User formUser,
                                BindingResult result,
                                Authentication authentication) {
        if (result.hasErrors()) {
            return "edit-profile";
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only update allowed fields
        user.setFirstName(formUser.getFirstName());
        user.setLastName(formUser.getLastName());
        user.setPhoneNumber(formUser.getPhoneNumber());
        user.setAddress(formUser.getAddress());

        userRepository.save(user);
        return "redirect:/profile/edit?success";
    }

    // Show Change Password Form
    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "change-password"; // Create this template
    }

    // Submit Change Password Form
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Authentication authentication,
                                 Model model) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            model.addAttribute("error", "Old password is incorrect");
            return "change-password";
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "redirect:/profile?passwordChanged";
    }

    // Security Settings Page (Optional)
    @GetMapping("/security")
    public String securitySettings(Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "security-settings"; // Optional: customize if needed
    }
}
