package com.bank.code.controller;

import com.bank.code.dto.request.BeneficiaryRequestDto;
import com.bank.code.dto.request.TransferRequestDto;
import com.bank.code.entity.Account;
import com.bank.code.entity.Beneficiary;
import com.bank.code.entity.User;
import com.bank.code.repository.AccountRepository;
import com.bank.code.repository.BeneficiaryRepository;
import com.bank.code.repository.UserRepository;
import com.bank.code.service.BeneficiaryService;
import com.bank.code.service.TransferService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/transfer")
public class TransferController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final BeneficiaryService beneficiaryService;
    private final TransferService transferService;

    public TransferController(UserRepository userRepository,
                              AccountRepository accountRepository,
                              BeneficiaryRepository beneficiaryRepository,
                              BeneficiaryService beneficiaryService,
                              TransferService transferService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.beneficiaryService = beneficiaryService;
        this.transferService = transferService;
    }

    // ----------------------------
    // GET: Show transfer page
    // ----------------------------
    @GetMapping
    public String showTransferPage(@RequestParam(name = "from", required = false) Long fromAccountId,
                                   @RequestParam(name = "message", required = false) String message,
                                   Principal principal,
                                   Model model) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Account> accounts = accountRepository.findByUserId(user.getUserId());
        List<Beneficiary> beneficiaries = beneficiaryRepository.findByUserId(user.getUserId());

        model.addAttribute("userAccounts", accounts);
        model.addAttribute("beneficiaries", beneficiaries);
        model.addAttribute("transferRequest", new TransferRequestDto());
        model.addAttribute("beneficiaryRequest", new BeneficiaryRequestDto());

        if (message != null) {
            model.addAttribute("message", message);
        }

        if (fromAccountId != null) {
            model.addAttribute("selectedAccountId", fromAccountId);
        }

        return "transfer";
    }


    // ----------------------------
    // POST: Perform money transfer
    // ----------------------------
    @PostMapping
    public String transferMoney(@ModelAttribute TransferRequestDto transferRequest,
                                Principal principal,
                                Model model) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Account> accounts = accountRepository.findByUserId(user.getUserId());
        List<Beneficiary> beneficiaries = beneficiaryRepository.findByUserId(user.getUserId());

        model.addAttribute("userAccounts", accounts);
        model.addAttribute("beneficiaries", beneficiaries);
        model.addAttribute("beneficiaryRequest", new BeneficiaryRequestDto());

        try {
            transferService.transfer(transferRequest);
            return "redirect:/transfer?message=Transfer Successful";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("transferRequest", new TransferRequestDto());
            return "transfer";
        }
    }

    // ----------------------------
    // POST: Add Beneficiary
    // ----------------------------
    @PostMapping("/add-beneficiary")
    public String addBeneficiary(@ModelAttribute BeneficiaryRequestDto beneficiaryRequest, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        beneficiaryService.addBeneficiary(user.getUserId(), beneficiaryRequest);
        return "redirect:/transfer?message=Beneficiary added successfully";
    }
    // ----------------------------
    // POST: Delete Beneficiary
    // ----------------------------
    @PostMapping("/delete-beneficiary")
    public String deleteBeneficiary(@RequestParam Long beneficiaryId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        beneficiaryService.deleteBeneficiary(beneficiaryId, user.getUserId());
        return "redirect:/transfer?message=Beneficiary deleted successfully";
    }

}
