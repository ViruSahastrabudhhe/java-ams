package com.dem_ams;

import com.dem_ams.domain.dto.LandlordProfileResponse;
import com.dem_ams.domain.dto.LandlordProfileRequest;
import com.dem_ams.domain.dto.UserModelResponse;
import com.dem_ams.domain.model.UserModel;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class DemAmsLandlordProfileTests {

    @Autowired
    private RestTestClient client;

    private RestTestClient clientWithBasicAuth(String username, String password) {
        return client.mutate()
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    @Test
    void shouldReturnExistingLandlordProfile() {
        EntityExchangeResult<LandlordProfileResponse> result = clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/landlords/me")
                .exchange()
                .expectStatus().isOk()
                .expectBody(LandlordProfileResponse.class)
                .returnResult();

        LandlordProfileResponse requestedLandlordProfile = result.getResponseBody();
        assertThat(requestedLandlordProfile).isNotNull();
        assertThat(requestedLandlordProfile.id()).isEqualTo(21);
        assertThat(requestedLandlordProfile.first_name()).isEqualTo("ALFRED");
        assertThat(requestedLandlordProfile.middle_name()).isEqualTo("LIM");
        assertThat(requestedLandlordProfile.last_name()).isEqualTo("LANDLORD");
        assertThat(requestedLandlordProfile.user_id()).isEqualTo(11);
    }

    @Test
    void shouldReturnLandlordProfileForAdmin() {
        EntityExchangeResult<LandlordProfileResponse> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/landlords/21")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LandlordProfileResponse.class)
                .returnResult();

        LandlordProfileResponse requestedLandlordProfile = response.getResponseBody();
        assertThat(requestedLandlordProfile).isNotNull();
        assertThat(requestedLandlordProfile.id()).isEqualTo(21L);
        assertThat(requestedLandlordProfile.first_name()).isEqualTo("ALFRED");
        assertThat(requestedLandlordProfile.middle_name()).isEqualTo("LIM");
        assertThat(requestedLandlordProfile.last_name()).isEqualTo("LANDLORD");
        assertThat(requestedLandlordProfile.user_id()).isEqualTo(11);
    }

    @Test
    void shouldReturnAListOfLandlordProfilesForAdmin() {
        EntityExchangeResult<String> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/landlords")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        int page = documentContext.read("$.page.totalElements");
        assertThat(page).isEqualTo(2);

        JSONArray ids = documentContext.read("$._embedded.landlordProfileResponseList[*].id");

        assertThat(ids).containsExactlyInAnyOrder(21, 22);
    }

    @Test
    void shouldNotReturnNonExistentLandlordProfile() {
        clientWithBasicAuth("landlord", "word123")
                .get()
                .uri("/api/landlords/22")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotAllowAccessToOtherLandlordProfile() {
        clientWithBasicAuth("renter", "pass456")
                .get()
                .uri("/api/landlords/11")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DirtiesContext
    void shouldCreateNewLandlordProfile() {
        EntityExchangeResult<UserModelResponse> userModelResult = clientWithBasicAuth("nolandlord", "lord123")
                .get()
                .uri("/api/users/me")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModelResponse.class)
                .returnResult();

        UserModelResponse userModelResponse = userModelResult.getResponseBody();
        assertThat(userModelResponse).isNotNull();
        assertThat(userModelResponse.username()).isEqualTo("nolandlord");
        assertThat(userModelResponse.user_id()).isEqualTo(15);

        Authentication principal = new UsernamePasswordAuthenticationToken("nolandlord", "lord123", AuthorityUtils.NO_AUTHORITIES);
        assertThat(userModelResponse.username()).isEqualTo(principal.getName());

        LandlordProfileRequest savedLandlordProfile = new LandlordProfileRequest("Nolandlord", "Mister", "Profile");

        EntityExchangeResult<LandlordProfileResponse> landlordProfileResult = clientWithBasicAuth("nolandlord", "lord123")
                .post()
                .uri("/api/landlords")
                .body(savedLandlordProfile)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LandlordProfileResponse.class)
                .returnResult();

        LandlordProfileResponse requestedLandlordProfile = landlordProfileResult.getResponseBody();
        assertThat(requestedLandlordProfile).isNotNull();
        assertThat(requestedLandlordProfile.id()).isEqualTo(1);
        assertThat(requestedLandlordProfile.first_name()).isEqualTo("Nolandlord");
        assertThat(requestedLandlordProfile.middle_name()).isEqualTo("Mister");
        assertThat(requestedLandlordProfile.last_name()).isEqualTo("Profile");
        assertThat(requestedLandlordProfile.user_id()).isEqualTo(15);
    }

    @Test
    @DirtiesContext
    void shouldUpdateExistingLandlordProfile() {
        LandlordProfileRequest savedLandlordProfileRequest = new LandlordProfileRequest("MICHAEL", "MAN", "LANDLORD");

        EntityExchangeResult<LandlordProfileResponse> result = clientWithBasicAuth("landlord", "word123")
                .put()
                .uri("/api/landlords")
                .body(savedLandlordProfileRequest)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LandlordProfileResponse.class)
                .returnResult();

        LandlordProfileResponse requestedLandlordProfile = result.getResponseBody();
        assertThat(requestedLandlordProfile).isNotNull();
        assertThat(requestedLandlordProfile.id()).isEqualTo(21);
        assertThat(requestedLandlordProfile.first_name()).isEqualTo("MICHAEL");
        assertThat(requestedLandlordProfile.middle_name()).isEqualTo("MAN");
        assertThat(requestedLandlordProfile.last_name()).isEqualTo("LANDLORD");
        assertThat(requestedLandlordProfile.user_id()).isEqualTo(11);
    }

    @Test
    @DirtiesContext
    void shouldDeleteExistingLandlordProfile() {
        clientWithBasicAuth("landlord", "word123")
                .delete()
                .uri("/api/landlords")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }
}
