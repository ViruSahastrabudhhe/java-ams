package com.dem_ams;

import com.dem_ams.domain.dto.PaymentRequest;
import com.dem_ams.domain.model.Payment;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class DemAmsPaymentTests {

    @Autowired
    private RestTestClient client;

    private static final Logger logger = Logger.getLogger(DemAmsPaymentTests.class.getName());

    private RestTestClient clientWithBasicAuth(String username, String password) {
        return client.mutate()
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    @Test
    void shouldReturnExistingPayment() {
        EntityExchangeResult<Payment> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments/90")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Payment.class)
                .returnResult();

        Payment payment = response.getResponseBody();
        assertThat(payment).isNotNull();
        assertThat(payment.getId()).isEqualTo(90L);
        assertThat(payment.getAmountPaid()).isEqualTo(100.00);
    }

    @Test
    void shouldNotReturnNonExistentPayment() {
        clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments/9919")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotReturnPaymentWhenUsingBadCredentials() {
        clientWithBasicAuth("admin", "bad-pw")
                .get()
                .uri("/api/payments/90")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isUnauthorized();

        clientWithBasicAuth("bad-user", "password")
                .get()
                .uri("/api/payments/90")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DirtiesContext
    void shouldCreateNewPayment() {
        PaymentRequest savedPaymentRequest = new PaymentRequest(500.00, 1L);

        clientWithBasicAuth("admin", "password")
                .post()
                .uri("/api/payments")
                .body(savedPaymentRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Void.class);

        EntityExchangeResult<Payment> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments/1")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Payment.class)
                .returnResult();

        Payment requestedPayment = response.getResponseBody();
        assertThat(requestedPayment).isNotNull();
        assertThat(requestedPayment.getId()).isEqualTo(1L);
        assertThat(requestedPayment.getAmountPaid()).isEqualTo(500.00);
    }

    @Test
    void shouldReturnAListOfPayments() {
        EntityExchangeResult<String> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        int page = documentContext.read("$.page.totalElements");
        assertThat(page).isEqualTo(4);

        JSONArray ids = documentContext.read("$._embedded.paymentResponseList[*].id");
        JSONArray amounts = documentContext.read("$._embedded.paymentResponseList[*].amountPaid");

        assertThat(ids).containsExactlyInAnyOrder(90, 91, 92, 93);
        assertThat(amounts).containsExactlyInAnyOrder(5.00, 110.10, 100.00, 250.00);

//        PagedModel<EntityModel<Payment>> paymentsList = response.getResponseBody();
//        assertThat(paymentsList).isNotNull();
//
//        PagedModel.PageMetadata embedded = paymentsList.getMetadata();
//        assertThat(embedded).isNotNull();
//
//        List<Long> ids = new ArrayList<>();
//        List<Double> amounts = new ArrayList<>();
//
//        for (EntityModel<Payment> payment : paymentsList.getContent()) {
//            ids.add(payment.getContent().getId());
//            amounts.add(payment.getContent().getAmount());
//        }
//
//        assertThat(embedded.getTotalElements()).isEqualTo(4);
//        assertThat(ids).containsExactlyInAnyOrder(90L, 91L, 92L, 93L);
//        assertThat(amounts).containsExactlyInAnyOrder(100.00, 5.00, 250.00, 110.10);
    }

    @Test
    void shouldReturnAPageOfPayments() {
        EntityExchangeResult<String> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments?page=0&size=1")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        int page = documentContext.read("$.page.size");
        assertThat(page).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfPayments() {
        EntityExchangeResult<String> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments?page=0&size=1&sort=amountPaid,desc")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        JSONArray read = documentContext.read("$._embedded.paymentResponseList");
        assertThat(read).isNotNull();
        assertThat(read.size()).isEqualTo(1);

        double amount = documentContext.read("$._embedded.paymentResponseList[0].amountPaid");
        assertThat(amount).isEqualTo(250.00);
    }

    @Test
    void shouldReturnASortedPageOfPaymentsWithoutParametersAndUseDefaultValues() {
        EntityExchangeResult<String> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        JSONArray read = documentContext.read("$._embedded.paymentResponseList");
        assertThat(read).isNotNull();
        assertThat(read.size()).isEqualTo(4);

        JSONArray amount = documentContext.read("$._embedded.paymentResponseList[*].amountPaid");
        assertThat(amount).containsExactly(5.00, 100.00, 110.10, 250.00);
    }

    @Test
    void shouldNotAllowAccessToPaymentTheyDoNotOwn() {
        clientWithBasicAuth("michael", "pass123")
                .get()
                .uri("/api/payments/91")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldRejectUnauthorizedUser() {
        clientWithBasicAuth("no-own-cars", "word123")
                .get()
                .uri("/api/payments/91")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(Payment.class)
                .returnResult();
    }

    @Test
    @DirtiesContext
    void shouldUpdateExistingPayment() {
        PaymentRequest paymentForm = new PaymentRequest(500.00, null);
        clientWithBasicAuth("admin", "password")
                .put()
                .uri("/api/payments/91")
                .body(paymentForm)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Payment.class);

        EntityExchangeResult<Payment> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments/91")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Payment.class)
                .returnResult();

        Payment payment = response.getResponseBody();
        assertThat(payment).isNotNull();

        assertThat(payment.getId()).isEqualTo(91L);
        assertThat(payment.getAmountPaid()).isEqualTo(500.00);
    }

    @Test
    void shouldNotAllowUsersUpdatingOtherUsersPayment() {
        PaymentRequest michaelsPayment = new PaymentRequest(670.67, null);
        clientWithBasicAuth("admin", "password")
                .put()
                .uri("/api/payments/94")
                .body(michaelsPayment)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotUpdateNonExistentPayment() {
        PaymentRequest paymentForm = new PaymentRequest(500.00, null);
        clientWithBasicAuth("admin", "password")
                .put()
                .uri("/api/payments/1007")
                .body(paymentForm)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DirtiesContext
    void shouldDeleteExistingPayment() {
        clientWithBasicAuth("admin", "password")
                .delete()
                .uri("/api/payments/91")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments/91")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotAllowUsersDeletingOtherUsersPayment() {
        clientWithBasicAuth("michael", "pass123")
                .delete()
                .uri("/api/payments/91")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }

    @Test
    void shouldNotDeleteNonExistentPayment() {
        clientWithBasicAuth("admin", "password")
                .delete()
                .uri("/api/payments/1007")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }
}
