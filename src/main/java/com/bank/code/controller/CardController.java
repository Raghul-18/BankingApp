package com.bank.code.controller;

import com.bank.code.entity.User;
import com.bank.code.service.CardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public String viewCards(Model model, @AuthenticationPrincipal User loggedInUser) {
        model.addAttribute("cards", cardService.getCardsByUser(loggedInUser));
        return "cards_template";
    }

    @PostMapping("/{cardId}/block")
    public String blockCard(@PathVariable Long cardId,
                            @AuthenticationPrincipal User loggedInUser) {
        cardService.blockCard(cardId, loggedInUser);
        return "redirect:/cards?message=Card blocked";
    }
}
