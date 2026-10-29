package com.dem_ams;

import com.dem_ams.domain.dto.LeaseRequest;
import com.dem_ams.domain.dto.LeaseResponse;
import com.dem_ams.domain.model.enums.BillingType;
import com.dem_ams.domain.model.Lease;
import com.dem_ams.domain.model.enums.LeaseStatus;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class DemAmsLeaseTests {

    @Autowired
    private RestTestClient client;

    private RestTestClient clientWithBasicAuth(String username, String password) {
        return client.mutate()
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    @Test
    void shouldReturnExistingLease() {
        EntityExchangeResult<LeaseResponse> result = clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/leases/40")
                .exchange()
                .expectStatus().isOk()
                .expectBody(LeaseResponse.class)
                .returnResult();

        LeaseResponse response = result.getResponseBody();
        assertThat(response).isNotNull();
    }

    @Test
    void shouldNotAllowLandlordAccessToLeaseTheyDoNotOwn() {
        clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/leases/42")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotReturnNonExistentLease() {
        clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/leases/712")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnAListOfLeases() {
        EntityExchangeResult<String> result = clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/leases")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        int page = documentContext.read("$.page.totalElements");
        assertThat(page).isEqualTo(3);

        JSONArray leases = documentContext.read("$._embedded.leaseResponseList[*].id");
        assertThat(leases).isNotNull();
        assertThat(leases).containsExactlyInAnyOrder(40, 41, 44);
        JSONArray units = documentContext.read("$._embedded.leaseResponseList[*].unit_id");
        assertThat(units).isNotNull();
        assertThat(units).containsExactlyInAnyOrder(70, 70, 71);
    }

    @Test
    void shouldReturnAPageOfLeases() {
        EntityExchangeResult<String> result = clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/leases?page=0&size=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        int page = documentContext.read("$.page.size");
        assertThat(page).isEqualTo(1);

        JSONArray leases = documentContext.read("$._embedded.leaseResponseList[*].id");
        assertThat(leases).isNotNull();
        assertThat(leases).containsExactly(40);
    }

    @Test
    void shouldReturnAListOfExistingTenantLeases() {
        EntityExchangeResult<String> result = clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/tenants/me/leases")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        int page = documentContext.read("$.page.totalElements");
        assertThat(page).isEqualTo(2);

        JSONArray ids = documentContext.read("$._embedded.leaseResponseList[*].id");
        assertThat(ids).containsExactlyInAnyOrder(40, 44);

        JSONArray propertyIds = documentContext.read("$._embedded.leaseResponseList[*].unit_id");
        assertThat(propertyIds).containsExactlyInAnyOrder(70, 70);
    }

    @Test
    void shouldReturnExistingTenantLease() {
        EntityExchangeResult<LeaseResponse> result = clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/tenants/me/leases/40")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LeaseResponse.class)
                .returnResult();

        LeaseResponse response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(40);
        assertThat(response.unit_id()).isEqualTo(70L);
        assertThat(response.start_date()).isEqualTo("2026-06-01");
        assertThat(response.end_date()).isEqualTo("2027-03-01");
        assertThat(response.billing_type()).isEqualTo(BillingType.MONTHLY);
        assertThat(response.rent()).isEqualTo(4500.00);
        assertThat(response.deposit()).isEqualTo(4500.0);
        assertThat(response.status()).isEqualTo(LeaseStatus.FINALIZED);
    }

    @Test
    void shouldNotReturnNonExistentTenantLease() {
        clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/tenants/me/leases/669")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotAllowAccessToTenantLeaseTheyDoNotOwn() {
        clientWithBasicAuth("renter", "pass456")
                .get()
                .uri("/api/tenants/me/leases/40")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DirtiesContext
    void shouldCreateNewLease() {
        LeaseRequest leaseRequest = new LeaseRequest(
                70L, 11L, LocalDate.of(2027, 4, 1), LocalDate.of(2027, 7, 1),
                BillingType.MONTHLY, 4500.00, 4500.00, LeaseStatus.DRAFT
        );

        EntityExchangeResult<LeaseResponse> leaseResponseResult = clientWithBasicAuth("landlord", "word123")
                .post()
                .uri("/api/leases")
                .body(leaseRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LeaseResponse.class)
                .returnResult();

        LeaseResponse leaseResponse = leaseResponseResult.getResponseBody();
        assertThat(leaseResponse).isNotNull();
        assertThat(leaseResponse.id()).isEqualTo(1);
        assertThat(leaseResponse.start_date()).isEqualTo("2027-04-01");
        assertThat(leaseResponse.end_date()).isEqualTo("2027-07-01");
        assertThat(leaseResponse.billing_type()).isEqualTo(BillingType.MONTHLY);
        assertThat(leaseResponse.rent()).isEqualTo(4500.00);
        assertThat(leaseResponse.deposit()).isEqualTo(4500.00);
        assertThat(leaseResponse.status()).isEqualTo(LeaseStatus.DRAFT);
        assertThat(leaseResponse.created_at()).isNotNull();

        EntityExchangeResult<String> tenantProfileResult = clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/tenants/me/leases")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(tenantProfileResult.getResponseBody());
        int page = documentContext.read("$.page.totalElements");
        assertThat(page).isEqualTo(3);

        JSONArray propertyIds =  documentContext.read("$._embedded.leaseResponseList[*].unit_id");
        assertThat(propertyIds).containsExactlyInAnyOrder(70, 70, 70);
    }

    @Test
    @DirtiesContext
    void shouldUpdateExistingLease() {
        LeaseRequest leaseRequest = new LeaseRequest(70L, 11L, LocalDate.of(2026, 6, 2),
                LocalDate.of(2027, 3, 2), BillingType.MONTHLY, 4501.00, 4502.00,  LeaseStatus.FINALIZED);

        EntityExchangeResult<LeaseResponse> result = clientWithBasicAuth("landlord", "word123")
                .put()
                .uri("/api/leases/40")
                .body(leaseRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LeaseResponse.class)
                .returnResult();

        LeaseResponse response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(40);
        assertThat(response.start_date()).isEqualTo("2026-06-02");
        assertThat(response.end_date()).isEqualTo("2027-03-02");
        assertThat(response.billing_type()).isEqualTo(BillingType.MONTHLY);
        assertThat(response.rent()).isEqualTo(4501.00);
        assertThat(response.deposit()).isEqualTo(4502.00);
        assertThat(response.status()).isEqualTo(LeaseStatus.FINALIZED);
        assertThat(response.created_at()).isNotNull();
        assertThat(response.updated_at()).isNotNull();
    }

    @Test
    void shouldNotUpdateNonExistentLease() {
        LeaseRequest leaseRequest = new LeaseRequest(70L, 11L, LocalDate.of(2026, 6, 2),
                LocalDate.of(2027, 3, 2), BillingType.MONTHLY, 4501.00, 4502.00,  LeaseStatus.FINALIZED);

        clientWithBasicAuth("landlord", "word123")
                .put()
                .uri("/api/leases/414")
                .body(leaseRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotUpdateLeaseTheyDoNotOwn() {
        LeaseRequest leaseRequest = new LeaseRequest(70L, 11L, LocalDate.of(2026, 6, 2),
                LocalDate.of(2027, 3, 2), BillingType.MONTHLY, 4501.00, 4502.00,  LeaseStatus.FINALIZED);

        clientWithBasicAuth("landlord", "word123")
                .put()
                .uri("/api/leases/42")
                .body(leaseRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DirtiesContext
    void shouldDeleteExistingLease() {
        clientWithBasicAuth("landlord", "word123")
                .delete()
                .uri("/api/leases/40")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldNotDeleteNonExistentLease() {
        clientWithBasicAuth("landlord", "word123")
                .delete()
                .uri("/api/leases/404")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotDeleteLeaseTheyDoNotOwn() {
        clientWithBasicAuth("landlord", "word123")
                .delete()
                .uri("/api/leases/42")
                .exchange()
                .expectStatus().isNotFound();
    }
}
