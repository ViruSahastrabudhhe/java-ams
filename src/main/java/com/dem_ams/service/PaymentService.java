package com.dem_ams.service;

import com.dem_ams.domain.dto.PaymentRequest;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.*;
import com.dem_ams.repository.InvoiceRepository;
import com.dem_ams.repository.PaymentRepository;
import com.dem_ams.repository.TenantProfileRepository;
import com.dem_ams.repository.UserModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
@Transactional
public class PaymentService {
    private static final Logger LOGGER = Logger.getLogger(PaymentService.class.getName());

    private final PaymentRepository paymentRepository;
    private final UserModelRepository userModelRepository;
    private final InvoiceRepository invoiceRepository;
    private final TenantProfileRepository tenantProfileRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            UserModelRepository userModelRepository,
            InvoiceRepository invoiceRepository,
            TenantProfileRepository tenantProfileRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.userModelRepository = userModelRepository;
        this.invoiceRepository = invoiceRepository;
        this.tenantProfileRepository = tenantProfileRepository;
    }

    public Payment getPaymentForUser(Long id, CustomUserDetails principal) {
        if (principal.isAdmin()) {
            return getPaymentById(id);
        }
        return getPaymentByIdAndUserId(id, principal.getId());
    }

    private Payment getPaymentByIdAndUserId(Long id, Long userId) {
        return paymentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find payment with id", id));
    }

    private Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find payment with id", id));
    }

    public Payment createPayment(PaymentRequest paymentRequest, Long userId) {
        UserModel userModel = userModelRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find user with id ", userId));
        Invoice invoice = invoiceRepository.findById(paymentRequest.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find invoice with id ", paymentRequest.invoiceId()));
        TenantProfile tenantProfile = tenantProfileRepository.findById(paymentRequest.tenantProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find tenant profile with id ", paymentRequest.tenantProfileId()));

        Payment paymentToCreate = new Payment();
        paymentToCreate.setAmountPaid(paymentRequest.amountPaid());
        paymentToCreate.setAmountChange(paymentRequest.amountChange());
        paymentToCreate.setPaymentType(paymentRequest.paymentType());
        paymentToCreate.setInvoice(invoice);
        paymentToCreate.setTenantProfile(tenantProfile);
        paymentToCreate.setUser(userModel);
        return paymentRepository.saveAndFlush(paymentToCreate);
    }

    public Page<Payment> getPaymentsForUser(CustomUserDetails principal, Pageable pageable) {
        if (principal.isAdmin()) {
            return getAllPayments(pageable);
        }
        return getPaymentsByUserId(principal.getId(), pageable);
    }

    private Page<Payment> getPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amountPaid"))
                ));
    }

    private Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amountPaid"))
                ));
    }

    public void updatePayment(Payment paymentToUpdate, PaymentRequest paymentRequest) {
        paymentToUpdate.setAmountPaid(paymentRequest.amountPaid());
        paymentToUpdate.setAmountChange(paymentRequest.amountChange());
        paymentToUpdate.setPaymentType(paymentRequest.paymentType());

        if (paymentRequest.invoiceId().describeConstable().isPresent()) {
            Invoice invoice = invoiceRepository.findById(paymentRequest.invoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Could not find invoice with id ", paymentRequest.invoiceId()));
            paymentToUpdate.setInvoice(invoice);
        }
        if (paymentRequest.tenantProfileId().describeConstable().isPresent()) {
            TenantProfile tenantProfile = tenantProfileRepository.findById(paymentRequest.tenantProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Could not find tenant profile with id ", paymentRequest.tenantProfileId()));
            paymentToUpdate.setTenantProfile(tenantProfile);
        }

        paymentRepository.saveAndFlush(paymentToUpdate);
    }

    public void deletePaymentById(Long id) {
        paymentRepository.deleteById(id);
    }
}
