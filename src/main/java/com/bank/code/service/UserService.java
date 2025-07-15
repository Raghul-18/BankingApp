package com.bank.code.service;

import com.bank.code.entity.Role;
import com.bank.code.entity.User;
import com.bank.code.repository.RoleRepository;
import com.bank.code.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Initialize default admin on first run
     */
    @PostConstruct
    public void initAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@bank.com", "admin123");
            admin.setPasswordHash(passwordEncoder.encode(admin.getPasswordHash()));
            admin.setAccountStatus("ACTIVE");
            admin.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));
            admin.setLoginAttempts(0);

            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ADMIN", "Administrator")));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);
            userRepository.save(admin);
        }
    }

    /**
     * Register a new user
     */
    public User registerUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setAccountStatus("ACTIVE");
        user.setLoginAttempts(0);
        user.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER", "Default user role")));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public void updateLastLogin(String username) {
        User u = findByUsername(username);
        if (u != null) {
            u.setLastLoginDate(new java.sql.Date(System.currentTimeMillis()));
            u.setLoginAttempts(0);
            userRepository.save(u);
        }
    }

    public void incrementLoginAttempts(String username) {
        User u = findByUsername(username);
        if (u != null) {
            u.setLoginAttempts(u.getLoginAttempts() + 1);
            if (u.getLoginAttempts() >= 5) {
                u.setAccountStatus("LOCKED");
            }
            userRepository.save(u);
        }
    }

    public void createDefaultRoles() {
        if (!roleRepository.existsByRoleName("ADMIN"))
            roleRepository.save(new Role("ADMIN", "Administrator role"));
        if (!roleRepository.existsByRoleName("USER"))
            roleRepository.save(new Role("USER", "Default user role"));
        if (!roleRepository.existsByRoleName("MANAGER"))
            roleRepository.save(new Role("MANAGER", "Manager role"));
    }

    /**
     * ✅ Correct Pagination with Accounts Loaded
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllWithAccounts(pageable);
    }

    /**
     * Not paginated - returns List<User>
     */
    public List<User> getAllUsersAsList() {
        return userRepository.findAll();
    }

    public long getTotalUsersCount() {
        return userRepository.count();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    public long getActiveUsersCount() {
        return userRepository.countByAccountStatus("ACTIVE");
    }

    public long getSuspendedUsersCount() {
        return userRepository.countByAccountStatus("SUSPENDED");
    }

    public Page<User> getUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }
}
