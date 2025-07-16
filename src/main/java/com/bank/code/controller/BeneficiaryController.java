package com.bank.code.controller;

import com.bank.code.dto.request.BeneficiaryRequestDto;
import com.bank.code.entity.Beneficiary;
import com.bank.code.entity.User;
import com.bank.code.repository.UserRepository;
import com.bank.code.service.BeneficiaryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/beneficiary")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;
    private final UserRepository userRepository;

    public BeneficiaryController(BeneficiaryService beneficiaryService, UserRepository userRepository) {
        this.beneficiaryService = beneficiaryService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public String addBeneficiary(@ModelAttribute BeneficiaryRequestDto beneficiaryRequest, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        beneficiaryService.addBeneficiary(user.getUserId(), beneficiaryRequest);
        return "redirect:/transfer?message=Beneficiary added successfully";
    }

    @PostMapping("/delete")
    public String deleteBeneficiary(@RequestParam Long beneficiaryId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        beneficiaryService.deleteBeneficiary(beneficiaryId, user.getUserId());
        return "redirect:/transfer?message=Beneficiary deleted successfully";
    }
}
