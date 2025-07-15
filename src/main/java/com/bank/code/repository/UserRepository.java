package com.bank.code.repository;

import com.bank.code.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
    @Query("SELECT u FROM User u WHERE u.accountStatus = 'ACTIVE'")
    List<User> findActiveUsers();
    long countByAccountStatus(String accountStatus); 
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.accounts")
    List<User> findAllWithAccounts();
    @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.accounts",
    	       countQuery = "SELECT COUNT(u) FROM User u")
    Page<User> findAllWithAccounts(Pageable pageable);

}
