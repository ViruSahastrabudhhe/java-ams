package com.dem_ams;

import com.dem_ams.domain.dto.ResidentialUnitDetails;
import com.dem_ams.domain.dto.UnitRequest;
import com.dem_ams.domain.model.enums.UnitStatus;
import com.dem_ams.domain.model.enums.UnitType;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class DemAmsResidentialUnitTests {

    @Autowired
    private RestTestClient client;

    private RestTestClient clientWithBasicAuth(String username, String password) {
        return client.mutate()
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    @Test
    void shouldReturnExistingResidentialUnit() {
        EntityExchangeResult<String> result = clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/units/70?type=residential")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        int id = documentContext.read("$.id");
        int propertyId = documentContext.read("$.property_id");
        String unitType = documentContext.read("$.unit_type");
        double squareMeters = documentContext.read("$.square_meters");
        double baseRent = documentContext.read("$.base_rent");
        String currency = documentContext.read("$.currency");
        String status = documentContext.read("$.status");
        String unitNumber = documentContext.read("$.unit_details.unit_number");
        int floor =  documentContext.read("$.unit_details.unit_floor");
        int bedroomQuantity = documentContext.read("$.unit_details.bedroom_quantity");
        int bathroomQuantity = documentContext.read("$.unit_details.bathroom_quantity");

        assertThat(id).isEqualTo(70);
        assertThat(propertyId).isEqualTo(60);
        assertThat(unitType).isEqualTo("RESIDENTIAL");
        assertThat(squareMeters).isEqualTo(80.0);
        assertThat(baseRent).isEqualTo(4500.00);
        assertThat(currency).isEqualTo("Philippine Peso");
        assertThat(status).isEqualTo(UnitStatus.OCCUPIED.name());
        assertThat(unitNumber).isEqualTo("1A");
        assertThat(floor).isEqualTo(1);
        assertThat(bathroomQuantity).isEqualTo(1);
        assertThat(bedroomQuantity).isEqualTo(2);
    }

    @Test
    void shouldNotReturnNonExistentResidentialUnit() {
        clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/units/717?type=residential")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotAllowAccessToResidentialUnitTheyDoNotOwn() {
        clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/units/73?type=residential")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DirtiesContext
    void shouldCreateNewResidentialUnit() {
        ResidentialUnitDetails residentialUnitDetails = new ResidentialUnitDetails("4A", 4, 2, 1);
        UnitRequest unitRequest = new UnitRequest(62L, UnitType.RESIDENTIAL, 80.0, 6500.0,
                "Philippine Peso", UnitStatus.VACANT, residentialUnitDetails);

        EntityExchangeResult<String> result = clientWithBasicAuth("landlord", "word123")
                .post()
                .uri("/api/units")
                .body(unitRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        int id = documentContext.read("$.id");
        int propertyId = documentContext.read("$.property_id");
        String unitType = documentContext.read("$.unit_type");
        double squareMeters = documentContext.read("$.square_meters");
        double baseRent = documentContext.read("$.base_rent");
        String currency = documentContext.read("$.currency");
        String status = documentContext.read("$.status");
        String unitNumber = documentContext.read("$.unit_details.unit_number");
        int floor =  documentContext.read("$.unit_details.unit_floor");
        int bedroomQuantity = documentContext.read("$.unit_details.bedroom_quantity");
        int bathroomQuantity = documentContext.read("$.unit_details.bathroom_quantity");

        assertThat(id).isEqualTo(1);
        assertThat(propertyId).isEqualTo(62);
        assertThat(unitType).isEqualTo("RESIDENTIAL");
        assertThat(squareMeters).isEqualTo(80.0);
        assertThat(baseRent).isEqualTo(6500.00);
        assertThat(currency).isEqualTo("Philippine Peso");
        assertThat(status).isEqualTo(UnitStatus.VACANT.name());
        assertThat(unitNumber).isEqualTo("4A");
        assertThat(floor).isEqualTo(4);
        assertThat(bedroomQuantity).isEqualTo(2);
        assertThat(bathroomQuantity).isEqualTo(1);
    }

    @Test
    void shouldUpdateResidentialUnit() {
        ResidentialUnitDetails residentialUnitDetails = new ResidentialUnitDetails("4A", 4, 2, 1);
        UnitRequest unitRequest = new UnitRequest(62L, UnitType.RESIDENTIAL, 80.0, 6500.0,
                "Philippine Peso", UnitStatus.OCCUPIED, residentialUnitDetails);

        EntityExchangeResult<String> result = clientWithBasicAuth("landlord", "word123")
                .put()
                .uri("/api/units/70?type=residential")
                .body(unitRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(result.getResponseBody());
        String status = documentContext.read("$.status");
        String dateUpdated = documentContext.read("$.updated_at");
        assertThat(status).isEqualTo(UnitStatus.OCCUPIED.name());
        assertThat(dateUpdated).isNotNull();
    }

    @Test
    void shouldNotUpdateResidentialUnitTheyDoNotOwn() {
        ResidentialUnitDetails residentialUnitDetails = new ResidentialUnitDetails("4A", 4, 2, 1);
        UnitRequest unitRequest = new UnitRequest(62L, UnitType.RESIDENTIAL, 80.0, 6500.0,
                "Philippine Peso", UnitStatus.OCCUPIED, residentialUnitDetails);

        clientWithBasicAuth("landlord", "word123")
                .put()
                .uri("/api/units/73?type=residential")
                .body(unitRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotUpdateNonExistentResidentialUnit() {
        ResidentialUnitDetails residentialUnitDetails = new ResidentialUnitDetails("4A", 4, 2, 1);
        UnitRequest unitRequest = new UnitRequest(62L, UnitType.RESIDENTIAL, 80.0, 6500.0,
                "Philippine Peso", UnitStatus.OCCUPIED, residentialUnitDetails);

        clientWithBasicAuth("landlord", "word123")
                .put()
                .uri("/api/units/999?type=residential")
                .body(unitRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DirtiesContext
    void shouldDeleteExistingResidentialUnit() {
        clientWithBasicAuth("landlord", "word123")
                .delete()
                .uri("/api/units/70?type=residential")
                .exchange()
                .expectStatus().isNoContent();

        clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/units/70?type=residential")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotDeleteResidentialUnitTheyDoNotOwn() {
        clientWithBasicAuth("landlord", "word123")
                .delete()
                .uri("/api/units/73?type=residential")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotDeleteNonExistentResidentialUnit() {
        clientWithBasicAuth("landlord", "word123")
                .delete()
                .uri("/api/units/711111?type=residential")
                .exchange()
                .expectStatus().isNotFound();
    }
}
