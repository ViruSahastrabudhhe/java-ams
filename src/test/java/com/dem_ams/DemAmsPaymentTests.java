package com.dem_ams;

import com.dem_ams.domain.dto.PaymentFormDTO;
import com.dem_ams.domain.model.Payment;
import com.dem_ams.domain.model.UserModel;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class DemAmsPaymentTests {

    @Autowired
    RestTestClient client;

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
                .exchange()
                .expectStatus().isOk()
                .expectBody(Payment.class)
                .returnResult();

        Payment payment = response.getResponseBody();
        assertThat(payment).isNotNull();
        assertThat(payment.getId()).isEqualTo(90L);
        assertThat(payment.getAmount()).isEqualTo(100.00);
    }

    @Test
    void shouldNotReturnNonExistentPayment() {
        clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments/9919")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotReturnPaymentWhenUsingBadCredentials() {
        clientWithBasicAuth("admin", "bad-pw")
                .get()
                .uri("/api/payments/90")
                .exchange()
                .expectStatus().isUnauthorized();

        clientWithBasicAuth("bad-user", "password")
                .get()
                .uri("/api/payments/90")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DirtiesContext
    void shouldCreateNewPayment() {
        PaymentFormDTO savedPaymentFormDTO = new PaymentFormDTO(500.00, 1L);

        clientWithBasicAuth("admin", "password")
                .post()
                .uri("/api/payments")
                .body(savedPaymentFormDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Void.class);

        EntityExchangeResult<Payment> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Payment.class)
                .returnResult();

        Payment requestedPayment = response.getResponseBody();
        assertThat(requestedPayment).isNotNull();
        assertThat(requestedPayment.getId()).isEqualTo(1L);
        assertThat(requestedPayment.getAmount()).isEqualTo(500.00);
    }

    @Test
    void shouldReturnAListOfPayments() {
        EntityExchangeResult<Iterable<Payment>> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Iterable<Payment>>() {})
                .returnResult();

        Iterable<Payment> paymentsList = response.getResponseBody();
        assertThat(paymentsList).isNotNull();

        int paymentCount = 0;
        List<Long> ids = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();

        for (Payment payment : paymentsList) {
            paymentCount++;
            ids.add(payment.getId());
            amounts.add(payment.getAmount());
        }

        assertThat(paymentCount).isEqualTo(4);
        assertThat(ids).containsExactlyInAnyOrder(90L, 91L, 92L, 93L);
        assertThat(amounts).containsExactlyInAnyOrder(100.00, 5.00, 250.00, 110.10);
    }

    @Test
    void shouldReturnAPageOfPayments() {
        EntityExchangeResult<String> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments?page=0&size=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page).isNotNull();
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfPayments() {
        EntityExchangeResult<String> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments?page=0&size=1&sort=amount,desc")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read).isNotNull();
        assertThat(read.size()).isEqualTo(1);

        double amount = documentContext.read("$[0].amount");
        assertThat(amount).isEqualTo(250.00);
    }

    @Test
    void shouldReturnASortedPageOfPaymentsWithoutParametersAndUseDefaultValues() {
        EntityExchangeResult<String> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/payments")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read).isNotNull();
        assertThat(read.size()).isEqualTo(4);

        JSONArray amount = documentContext.read("$..amount");
        assertThat(amount).containsExactly(5.00, 100.00, 110.10, 250.00);
    }

    @Test
    void shouldNotAllowAccessToPaymentTheyDoNotOwn() {
        clientWithBasicAuth("michael", "pass123")
                .get()
                .uri("/api/payments/91")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Payment.class)
                .returnResult();
    }

    @Test
    void shouldRejectUnauthorizedUser() {
        clientWithBasicAuth("no-own-cars", "word123")
                .get()
                .uri("/api/payments/91")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(Payment.class)
                .returnResult();
    }
}
