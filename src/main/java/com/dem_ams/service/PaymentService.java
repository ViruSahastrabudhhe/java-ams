package com.dem_ams.service;

import com.dem_ams.domain.dto.PaymentRequest;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.Invoice;
import com.dem_ams.domain.model.Payment;
import com.dem_ams.domain.model.TenantProfile;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.InvoiceRepository;
import com.dem_ams.repository.PaymentRepository;
import com.dem_ams.repository.TenantProfileRepository;
import com.dem_ams.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final TenantProfileRepository tenantProfileRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            InvoiceRepository invoiceRepository,
            TenantProfileRepository tenantProfileRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.tenantProfileRepository = tenantProfileRepository;
    }

    public Payment getPaymentByIdAndUserId(Long id, Long userID) {
        return paymentRepository.findByIdAndUserId(id, userID)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find payment with id", id));
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find payment with id", id));
    }

    public Payment createPayment(PaymentRequest paymentRequest, Long userID) {
        UserModel userModel = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find user with id ", userID));
        Invoice invoice = invoiceRepository.findById(paymentRequest.invoice_id())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find invoice with id ", paymentRequest.invoice_id()));
        TenantProfile tenantProfile = tenantProfileRepository.findById(paymentRequest.tenant_profile_id())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find tenant profile with id ", paymentRequest.tenant_profile_id()));

        Payment createdPayment = new Payment();
        createdPayment.setAmountPaid(paymentRequest.amountPaid());
        createdPayment.setAmountChange(paymentRequest.amountChange());
        createdPayment.setPaymentType(paymentRequest.paymentType());
        createdPayment.setInvoice(invoice);
        createdPayment.setTenantProfile(tenantProfile);
        createdPayment.setUser(userModel);
        return paymentRepository.saveAndFlush(createdPayment);
    }

    public void updatePayment(Payment paymentToUpdate, PaymentRequest paymentRequest) {
        paymentToUpdate.setAmountPaid(paymentRequest.amountPaid());
        paymentToUpdate.setAmountChange(paymentRequest.amountChange());
        paymentToUpdate.setPaymentType(paymentRequest.paymentType());

        if (paymentRequest.invoice_id().describeConstable().isPresent()) {
            Invoice invoice = invoiceRepository.findById(paymentRequest.invoice_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Could not find invoice with id ", paymentRequest.invoice_id()));
            paymentToUpdate.setInvoice(invoice);
        }
        if (paymentRequest.tenant_profile_id().describeConstable().isPresent()) {
            TenantProfile tenantProfile = tenantProfileRepository.findById(paymentRequest.tenant_profile_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Could not find tenant profile with id ", paymentRequest.tenant_profile_id()));
            paymentToUpdate.setTenantProfile(tenantProfile);
        }

        paymentRepository.saveAndFlush(paymentToUpdate);
    }

    public Page<Payment> getPayments(Long userID, Pageable pageable) {
        return paymentRepository.findByUserId(userID,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amountPaid"))
                ));
    }

    public Page<Payment> getPaymentsNoUserId(Pageable pageable) {
        return paymentRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amountPaid"))
                ));
    }

    public boolean existsById(Long id) {
        return paymentRepository.existsById(id);
    }

    public void deletePaymentById(Long id) {
        paymentRepository.deleteById(id);
    }
}
