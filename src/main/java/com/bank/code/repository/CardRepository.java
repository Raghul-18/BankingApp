package com.bank.code.repository;

import com.bank.code.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByAccountUserUserId(Long userId);
}
