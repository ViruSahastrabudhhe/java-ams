package com.dem_ams;

import com.dem_ams.domain.dto.PaymentResponse;
import com.dem_ams.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DemAmsPaymentJsonTests {
    @Autowired
    private JacksonTester<PaymentResponse> jsonPayment;

    @Autowired
    private JacksonTester<List<PaymentResponse>> jsonPaymentsList;

    private List<PaymentResponse> paymentResponsesList;

    @BeforeEach
    void setup() {
        UserModel user = UserModel.builder()
                .id(3L)
                .email("tenant@example.com")
                .username("tenant")
                .password("pass123")
                .build();

        LocalDateTime defaultDate = LocalDateTime.of(2026, 9, 25, 0, 37, 27, 614000000);

        List<TenantProfile> tenantProfiles = List.of(
                createTenantProfile(11L, "JOHN", "MAN", "TENANT", user)
        );

        List<Invoice> invoices = List.of(
                createInvoice(110L, 4500.00, Invoice.InvoiceStatus.PAID, LocalDate.of(2026, 6, 1), tenantProfiles.get(0), user, LocalDateTime.of(2026, 6, 1, 0, 37, 27, 614_000_000)),
                createInvoice(111L, 4500.00, Invoice.InvoiceStatus.PAID, LocalDate.of(2026, 7, 1), tenantProfiles.get(0), user, LocalDateTime.of(2026, 7, 1, 0, 37, 27, 614_000_000)),
                createInvoice(112L, 4500.00, Invoice.InvoiceStatus.PAID, LocalDate.of(2026, 8, 1), tenantProfiles.get(0), user, LocalDateTime.of(2026, 8, 1, 0, 37, 27, 614_000_000)),
                createInvoice(113L, 4500.00, Invoice.InvoiceStatus.PAID, LocalDate.of(2026, 9, 1), tenantProfiles.get(0), user, LocalDateTime.of(2026, 9, 1, 0, 37, 27, 614_000_000))
        );

        List<Payment> payments = List.of(
                createPayment(90L, 100.00, Payment.PaymentType.CASH, invoices.get(0), tenantProfiles.get(0), user, defaultDate),
                createPayment(91L, 5.00, Payment.PaymentType.CASH, invoices.get(1), tenantProfiles.get(0), user, defaultDate),
                createPayment(92L, 250.00, Payment.PaymentType.CASH, invoices.get(2), tenantProfiles.get(0), user, defaultDate),
                createPayment(93L, 110.10, Payment.PaymentType.CASH, invoices.get(3), tenantProfiles.get(0), user, defaultDate)
        );

        paymentResponsesList = payments.stream()
                .map(PaymentResponse::from)
                .toList();
    }

    private static TenantProfile createTenantProfile(
            Long id,
            String firstName,
            String middleName,
            String lastName,
            UserModel user) {
        TenantProfile tenantProfile = new TenantProfile();

        tenantProfile.setId(id);
        tenantProfile.setFirstName(firstName);
        tenantProfile.setMiddleName(middleName);
        tenantProfile.setLastName(lastName);
        tenantProfile.setUser(user);

        return tenantProfile;
    }

    private static Invoice createInvoice(
            Long id,
            Double invoiceTotal,
            Invoice.InvoiceStatus invoiceStatus,
            LocalDate dueDate,
            TenantProfile tenantProfile,
            UserModel user,
            LocalDateTime createdAt) {
        Invoice invoice = new Invoice();

        invoice.setId(id);
        invoice.setInvoiceTotal(invoiceTotal);
        invoice.setInvoiceStatus(invoiceStatus);
        invoice.setDueDate(dueDate);
        invoice.setTenantProfile(tenantProfile);
        invoice.setUser(user);
        invoice.setCreatedAt(createdAt);

        return invoice;
    }

    private static Payment createPayment(
            Long id,
            Double amountPaid,
            Payment.PaymentType paymentType,
            Invoice invoice,
            TenantProfile tenantProfile,
            UserModel user,
            LocalDateTime date) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setAmountPaid(amountPaid);
        payment.setAmountChange(0.0);
        payment.setPaymentType(paymentType);
        payment.setInvoice(invoice);
        payment.setTenantProfile(tenantProfile);
        payment.setUser(user);
        payment.setCreatedAt(date);
        return payment;
    }

    @Test
    void paymentSerializationTest() throws IOException {
        PaymentResponse payment = paymentResponsesList.getFirst();

        assertThat(jsonPayment.write(payment)).isStrictlyEqualToJson("payments_expected.json");
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.id");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(90);
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.amount_paid");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.amount_paid")
                .isEqualTo(100.00);
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.amount_change");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.amount_change")
                .isEqualTo(0.0);
        assertThat(jsonPayment.write(payment)).hasJsonPathStringValue("@.payment_type");
        assertThat(jsonPayment.write(payment)).extractingJsonPathStringValue("@.payment_type")
                .isEqualTo(Payment.PaymentType.CASH.name());
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.invoice_id");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.invoice_id")
                .isEqualTo(110);
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.tenant_profile_id");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.tenant_profile_id")
                .isEqualTo(11);
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.user_id");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.user_id")
                .isEqualTo(3);
        assertThat(jsonPayment.write(payment)).hasJsonPathStringValue("@.created_at");
        assertThat(jsonPayment.write(payment)).extractingJsonPathStringValue("@.created_at")
                .isEqualTo("2026-09-25T00:37:27.614");
    }

    @Test
    void paymentDeserializationTest() throws IOException {
        String expected = """
                {
                  "id": 90,
                  "amount_paid": 100.00,
                  "amount_change": 0.0,
                  "payment_type": "CASH",
                  "invoice_id": 110,
                  "tenant_profile_id": 11,
                  "user_id": 3,
                  "created_at": "2026-09-25T00:37:27.614",
                  "updated_at": null,
                  "deleted_at": null
                }
                """;

        PaymentResponse payment = paymentResponsesList.getFirst();

        assertThat(jsonPayment.parse(expected)).isEqualTo(payment);
        assertThat(jsonPayment.parseObject(expected).id()).isEqualTo(90L);
        assertThat(jsonPayment.parseObject(expected).amount_paid()).isEqualTo(100.00);
        assertThat(jsonPayment.parseObject(expected).amount_change()).isEqualTo(0.00);
        assertThat(jsonPayment.parseObject(expected).payment_type()).isEqualTo(Payment.PaymentType.CASH);
        assertThat(jsonPayment.parseObject(expected).invoice_id()).isEqualTo(110L);
        assertThat(jsonPayment.parseObject(expected).tenant_profile_id()).isEqualTo(11L);
        assertThat(jsonPayment.parseObject(expected).user_id()).isEqualTo(3L);
    }

    @Test
    void paymentListSerializationTest() throws IOException {
        assertThat(jsonPaymentsList.write(paymentResponsesList)).isStrictlyEqualToJson("payments_list.json");
    }

    @Test
    void paymentListDeserializationTest() throws IOException {
        String list = """
                    [
                      {"id":  90, "amount_paid":  100.00, "amount_change": 0.0, "payment_type": "CASH", "invoice_id": 110, "tenant_profile_id": 11, "user_id": 3, "created_at": "2026-09-25T00:37:27.614", "updated_at": null, "deleted_at": null },
                      {"id":  91, "amount_paid":  5.00, "amount_change": 0.0, "payment_type": "CASH", "invoice_id": 111, "tenant_profile_id": 11, "user_id": 3, "created_at": "2026-09-25T00:37:27.614", "updated_at": null, "deleted_at": null },
                      {"id":  92, "amount_paid":  250.00, "amount_change": 0.0, "payment_type": "CASH", "invoice_id": 112, "tenant_profile_id": 11, "user_id": 3, "created_at": "2026-09-25T00:37:27.614", "updated_at": null, "deleted_at": null },
                      {"id":  93, "amount_paid":  110.10, "amount_change": 0.0, "payment_type": "CASH", "invoice_id": 113, "tenant_profile_id": 11, "user_id": 3, "created_at": "2026-09-25T00:37:27.614", "updated_at": null, "deleted_at": null }
                    ]
                """;

        assertThat(jsonPaymentsList.parse(list)).isEqualTo(paymentResponsesList);
    }

}
