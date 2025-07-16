package com.bank.code.service;

import com.bank.code.dto.request.BeneficiaryRequestDto;
import com.bank.code.entity.Beneficiary;
import com.bank.code.repository.BeneficiaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public List<Beneficiary> getUserBeneficiaries(Long userId) {
        return beneficiaryRepository.findByUserId(userId);
    }

    public Beneficiary addBeneficiary(Long userId, BeneficiaryRequestDto dto) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUserId(userId);
        beneficiary.setName(dto.getName());
        beneficiary.setAccountNumber(dto.getAccountNumber());
        beneficiary.setBankName(dto.getBankName());
        beneficiary.setIfscCode(dto.getIfscCode());

        return beneficiaryRepository.save(beneficiary);
    }

    public void deleteBeneficiary(Long beneficiaryId, Long userId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found"));

        if (!beneficiary.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized deletion attempt.");
        }

        beneficiaryRepository.delete(beneficiary);
    }
}
