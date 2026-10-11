package com.dem_ams;

import com.dem_ams.domain.dto.PropertyRequest;
import com.dem_ams.domain.dto.PropertyResponse;
import com.dem_ams.domain.model.BillingType;
import com.dem_ams.domain.model.LandlordProfile;
import com.dem_ams.domain.model.Property;
import com.dem_ams.domain.model.PropertyDetail;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class DemAmsPropertyTests {

    @Autowired
    private RestTestClient client;

    private RestTestClient clientWithBasicAuth(String username, String password) {
        return client.mutate()
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    @Test
    void shouldReturnExistingProperty() {
        EntityExchangeResult<PropertyResponse> result = clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/properties/60")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PropertyResponse.class)
                .returnResult();

        PropertyResponse response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(60);
        assertThat(response.name()).isEqualTo("Santa Cruz Apartment");
        assertThat(response.property_type()).isEqualTo("Apartments");
        assertThat(response.property_value()).isEqualTo(4500.00);
        assertThat(response.billing_type()).isEqualTo(BillingType.MONTHLY);
        assertThat(response.bedroom_quantity()).isEqualTo(2);
        assertThat(response.bathroom_quantity()).isEqualTo(1);
        assertThat(response.size()).isEqualTo("80");
        assertThat(response.description()).isNotNull();
        assertThat(response.created_at()).isNotNull();
    }

    @Test
    void shouldNotAllowLandlordAccessToPropertyTheyDoNotOwn() {
        clientWithBasicAuth("lender", "lend123")
                .get()
                .uri("/api/properties/60")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotReturnNonExistentProperty() {
        clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/properties/9191")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnAListOfPropertiesForLandlord() {
        EntityExchangeResult<String> result = clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/properties")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        int page = documentContext.read("$.page.totalElements");
        assertThat(page).isEqualTo(3);

        JSONArray ids = documentContext.read("$._embedded.propertyResponseList[*].id");
        JSONArray landlordProfileIds = documentContext.read("$._embedded.propertyResponseList[*].landlord_profile_id");

        assertThat(ids).containsExactlyInAnyOrder(60, 61, 62);
        assertThat(landlordProfileIds).containsExactlyInAnyOrder(21,21,21);
    }

    @Test
    void shouldReturnAListOfPropertiesForTenant() {
        EntityExchangeResult<String> result = clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/properties")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        int page = documentContext.read("$.page.totalElements");
        assertThat(page).isEqualTo(4);

        JSONArray ids = documentContext.read("$._embedded.propertyResponseList[*].id");
        assertThat(ids).containsExactlyInAnyOrder(60,61,62,63);
    }

    @Test
    void shouldReturnAListOfPropertiesForAdmin() {
        EntityExchangeResult<String> result = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/properties")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        int page = documentContext.read("$.page.totalElements");
        assertThat(page).isEqualTo(4);

        JSONArray ids = documentContext.read("$._embedded.propertyResponseList[*].id");
        assertThat(ids).containsExactlyInAnyOrder(60,61,62,63);
    }

    @Test
    void shouldReturnASortedPageOfPropertiesSortedByPropertyValue() {
        EntityExchangeResult<String> result = clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/properties?page=0&size=1&sort=propertyValue,desc")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        JSONArray read = documentContext.read("$._embedded.propertyResponseList");
        assertThat(read).isNotNull();
        assertThat(read.size()).isEqualTo(1);

        double amount = documentContext.read("$._embedded.propertyResponseList[0].property_value");
        assertThat(amount).isEqualTo(8000.00);
    }

    @Test
    void shouldReturnASortedPageOfPropertiesSortedByName() {
        EntityExchangeResult<String> result = clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/properties?page=0&size=1&sort=name,asc")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        JSONArray read = documentContext.read("$._embedded.propertyResponseList");
        assertThat(read).isNotNull();
        assertThat(read.size()).isEqualTo(1);

        String name = documentContext.read("$._embedded.propertyResponseList[0].name");
        assertThat(name).isEqualTo("Lynville Apartment Unit No. 1");
    }

    @Test
    void shouldReturnASortedPageOfPropertiesWithoutParametersAndUseDefaultValues() {
        EntityExchangeResult<String> response = clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/properties")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        JSONArray read = documentContext.read("$._embedded.propertyResponseList");
        assertThat(read).isNotNull();
        assertThat(read.size()).isEqualTo(4);

        JSONArray ids = documentContext.read("$._embedded.propertyResponseList[*].id");
        assertThat(ids).containsExactly(60,61,62,63);
    }

    @Test
    @DirtiesContext
    void shouldCreateNewProperty() {
        PropertyRequest savedProperty = new PropertyRequest(
                "Maxmini Apartment", "Apartments", 9900.00, BillingType.MONTHLY,
                2, 1, "60 sqm", "A studio apartment in Manila."
        );

        EntityExchangeResult<PropertyResponse> result = clientWithBasicAuth("landlord", "word123")
                .post()
                .uri("/api/properties")
                .body(savedProperty)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PropertyResponse.class)
                .returnResult();

        PropertyResponse requestedProperty = result.getResponseBody();
        assertThat(requestedProperty).isNotNull();
        assertThat(requestedProperty.name()).isEqualTo("Maxmini Apartment");
        assertThat(requestedProperty.property_type()).isEqualTo("Apartments");
        assertThat(requestedProperty.property_value()).isEqualTo(9900.00);
        assertThat(requestedProperty.billing_type()).isEqualTo(BillingType.MONTHLY);
        assertThat(requestedProperty.bedroom_quantity()).isEqualTo(2);
        assertThat(requestedProperty.bathroom_quantity()).isEqualTo(1);
        assertThat(requestedProperty.size()).isEqualTo("60 sqm");
        assertThat(requestedProperty.description()).isNotNull();
        assertThat(requestedProperty.created_at()).isNotNull();
    }

    @Test
    @DirtiesContext
    void shouldUpdateExistingProperty() {
        PropertyRequest savedProperty = new PropertyRequest(
                "Sorter Family Apartment Unit 1", "Apartments", 4750.00, BillingType.MONTHLY,
                2, 1, "80", "Apartment in Santa Cruz."
        );

        EntityExchangeResult<PropertyResponse> result = clientWithBasicAuth("landlord", "word123")
                .put()
                .uri("/api/properties/60")
                .body(savedProperty)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PropertyResponse.class)
                .returnResult();

        PropertyResponse requestedProperty = result.getResponseBody();
        assertThat(requestedProperty).isNotNull();
        assertThat(requestedProperty.name()).isEqualTo("Sorter Family Apartment Unit 1");
        assertThat(requestedProperty.property_type()).isEqualTo("Apartments");
        assertThat(requestedProperty.property_value()).isEqualTo(4750.00);
        assertThat(requestedProperty.billing_type()).isEqualTo(BillingType.MONTHLY);
        assertThat(requestedProperty.bedroom_quantity()).isEqualTo(2);
        assertThat(requestedProperty.bathroom_quantity()).isEqualTo(1);
        assertThat(requestedProperty.size()).isEqualTo("80");
        assertThat(requestedProperty.description()).isNotNull();
        assertThat(requestedProperty.created_at()).isNotNull();
        assertThat(requestedProperty.updated_at()).isNotNull();
    }

    @Test
    void shouldNotAllowTenantToUpdateProperty() {
        PropertyRequest savedProperty = new PropertyRequest(
                "Sorter Family Apartment Unit 1", "Apartments", 4750.00, BillingType.MONTHLY,
                2, 1, "80", "Apartment in Santa Cruz."
        );

        clientWithBasicAuth("tenant", "pass123")
                .put()
                .uri("/api/properties/60")
                .body(savedProperty)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldNotUpdatePropertyTheyDoNotOwn() {
        PropertyRequest savedProperty = new PropertyRequest(
                "Sorter Family Apartment Unit 1", "Apartments", 4750.00, BillingType.MONTHLY,
                2, 1, "80", "Apartment in Santa Cruz."
        );

        clientWithBasicAuth("lender", "lend123")
                .put()
                .uri("/api/properties/60")
                .body(savedProperty)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotUpdateNonExistentProperty() {
        PropertyRequest savedProperty = new PropertyRequest(
                "Sorter Family Apartment Unit 1", "Apartments", 4750.00, BillingType.MONTHLY,
                2, 1, "80", "Apartment in Santa Cruz."
        );

        clientWithBasicAuth("landlord", "word123")
                .put()
                .uri("/api/properties/7717")
                .body(savedProperty)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DirtiesContext
    void shouldDeleteExistingProperty() {
        clientWithBasicAuth("landlord", "word123")
                .delete()
                .uri("/api/properties/60")
                .exchange()
                .expectStatus().isNoContent();

        clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/properties/60")
                .exchange()
                .expectStatus().isNotFound();
    }
}
