package com.dem_ams.service;

import com.dem_ams.domain.dto.PaymentRequest;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.Payment;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.PaymentRepository;
import com.dem_ams.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            UserRepository userRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public Payment getPaymentByIdAndUserId(Long id, Long userID) {
        return paymentRepository.findByIdAndUserId(id, userID)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find payment with id", id));
    }

    public Payment createPayment(PaymentRequest paymentRequest, Long userID) {
        UserModel userModel = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find user with id ", userID));

        Payment createdPayment = new Payment();
        createdPayment.setAmountPaid(paymentRequest.amountPaid());
        createdPayment.setUser(userModel);
        return paymentRepository.save(createdPayment);
    }

    public void updatePayment(Long id, PaymentRequest paymentRequest, Long userID) {
        UserModel userModel = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find user with id ", userID));

        Payment updatedPayment = new Payment();
        updatedPayment.setId(id);
        updatedPayment.setAmountPaid(paymentRequest.amountPaid());
        updatedPayment.setUser(userModel);
        paymentRepository.save(updatedPayment);
    }

    public Page<Payment> getPayments(Long userID, Pageable pageable) {
        return paymentRepository.findByUserId(userID,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amountPaid"))
                ));
    }

    public boolean existsByIdAndUserId(Long id, Long userID) {
        return paymentRepository.existsByIdAndUserId(id, userID);
    }

    public void deletePaymentById(Long id) {
        paymentRepository.deleteById(id);
    }
}
