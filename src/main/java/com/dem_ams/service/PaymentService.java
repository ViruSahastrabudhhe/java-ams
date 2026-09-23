package com.dem_ams.service;

import com.dem_ams.domain.dto.PaymentFormDTO;
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

    public Payment getPaymentByIdAndUserId(Long id, Long user_id) {
        Optional<Payment> optionalPayment = paymentRepository.findByIdAndUserId(id, user_id);
        return optionalPayment.orElse(null);
    }

    public Payment createPayment(PaymentFormDTO paymentFormDTO, Long userID) {
        Payment payment = new Payment(null, paymentFormDTO.amount(), userID);
        return paymentRepository.save(payment);
    }

    public Page<Payment> getPayments(Long user_id, Pageable pageable) {
        return paymentRepository.findByUserId(user_id,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
    }

    public void deletePayment(Payment payment) {
        paymentRepository.delete(payment);
    }

}
