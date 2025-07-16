package com.bank.code.service;

import com.bank.code.entity.Card;
import com.bank.code.entity.User;
import com.bank.code.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> getCardsByUser(User user) {
        return cardRepository.findByAccountUserUserId(user.getUserId());
    }

    @Transactional
    public void blockCard(Long cardId, User user) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getAccount().getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized card access");
        }

        card.setStatus("BLOCKED");
        cardRepository.save(card);
    }
}
