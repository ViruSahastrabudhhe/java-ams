package com.dem_ams.service;

import com.dem_ams.domain.dto.PaymentFormDTO;
import com.dem_ams.domain.exception.PaymentNotFoundException;
import com.dem_ams.domain.model.Payment;
import com.dem_ams.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment getPaymentByIdAndUserId(Long id, Long userID) {
        return paymentRepository.findByIdAndUserId(id, userID)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public Payment createPayment(PaymentFormDTO paymentFormDTO, Long userID) {
        Payment createdPayment = new Payment(null, paymentFormDTO.amount(), userID);
        return paymentRepository.save(createdPayment);
    }

    public void updatePayment(Long id, PaymentFormDTO paymentFormDTO, Long userID) {
        Payment updatedPayment = new Payment(id, paymentFormDTO.amount(), userID);
        paymentRepository.save(updatedPayment);
    }

    public Page<Payment> getPayments(Long userID, Pageable pageable) {
        return paymentRepository.findByUserId(userID,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
    }

    public boolean existsByIdAndUserId(Long id, Long userID) {
        return paymentRepository.existsByIdAndUserId(id, userID);
    }

    public void deletePaymentById(Long id) {
        paymentRepository.deleteById(id);
    }
}
