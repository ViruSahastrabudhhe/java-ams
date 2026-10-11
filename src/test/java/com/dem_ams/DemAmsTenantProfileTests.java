package com.dem_ams;

import com.dem_ams.domain.dto.TenantProfileRequest;
import com.dem_ams.domain.dto.TenantProfileResponse;
import com.dem_ams.domain.model.TenantProfile;
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
public class DemAmsTenantProfileTests {

    @Autowired
    private RestTestClient client;

    private RestTestClient clientWithBasicAuth(String username, String password) {
        return client.mutate()
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    @Test
    void shouldReturnExistingTenantProfile() {
        EntityExchangeResult<TenantProfileResponse> response = clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/tenants/me")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TenantProfileResponse.class)
                .returnResult();

        TenantProfileResponse requestedTenantProfile = response.getResponseBody();
        assertThat(requestedTenantProfile).isNotNull();
        assertThat(requestedTenantProfile.id()).isEqualTo(11L);
        assertThat(requestedTenantProfile.first_name()).isEqualTo("JOHN");
        assertThat(requestedTenantProfile.middle_name()).isEqualTo("MAN");
        assertThat(requestedTenantProfile.last_name()).isEqualTo("TENANT");
    }

    @Test
    void shouldReturnTenantProfileForAdmin() {
        EntityExchangeResult<TenantProfileResponse> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/tenants/11")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TenantProfileResponse.class)
                .returnResult();

        TenantProfileResponse requestedTenantProfile = response.getResponseBody();
        assertThat(requestedTenantProfile).isNotNull();
        assertThat(requestedTenantProfile.id()).isEqualTo(11L);
        assertThat(requestedTenantProfile.first_name()).isEqualTo("JOHN");
        assertThat(requestedTenantProfile.middle_name()).isEqualTo("MAN");
        assertThat(requestedTenantProfile.last_name()).isEqualTo("TENANT");
        assertThat(requestedTenantProfile.user_id()).isEqualTo(3);
    }

    @Test
    void shouldReturnAListOfTenantProfilesForAdmin() {
        EntityExchangeResult<String> response = clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/tenants")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        DocumentContext documentContext = JsonPath.parse(response.getResponseBody());
        int page = documentContext.read("$.page.totalElements");
        assertThat(page).isEqualTo(2);

        JSONArray ids = documentContext.read("$._embedded.tenantProfileResponseList[*].id");

        assertThat(ids).containsExactlyInAnyOrder(11, 12);
    }

    @Test
    void shouldNotReturnNonExistentTenantProfile() {
        clientWithBasicAuth("admin", "password")
                .get()
                .uri("/api/tenants/74")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotAllowAccessToOtherTenantProfile() {
        clientWithBasicAuth("renter", "pass456")
                .get()
                .uri("/api/tenants/11")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DirtiesContext
    void shouldCreateNewTenantProfile() {
        EntityExchangeResult<UserModel> userModelResult = clientWithBasicAuth("notenant", "pass123")
                .get()
                .uri("/api/users/5")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModel.class)
                .returnResult();

        UserModel userModel = userModelResult.getResponseBody();
        assertThat(userModel).isNotNull();
        assertThat(userModel.getUsername()).isEqualTo("notenant");
        assertThat(userModel.getPassword()).isEqualTo("{noop}pass123");
        assertThat(userModel.getId()).isEqualTo(5);

        Authentication principal = new UsernamePasswordAuthenticationToken("notenant", "pass123", AuthorityUtils.NO_AUTHORITIES);
        assertThat(userModel.getUsername()).isEqualTo(principal.getName());

        TenantProfileRequest savedTenantProfile = new TenantProfileRequest("Notenant", "Mister", "Profile");

        EntityExchangeResult<TenantProfileResponse> tenantProfileResult = clientWithBasicAuth("notenant", "pass123")
                .post()
                .uri("/api/tenants")
                .body(savedTenantProfile)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TenantProfileResponse.class)
                .returnResult();

        TenantProfileResponse requestedTenantProfile = tenantProfileResult.getResponseBody();
        assertThat(requestedTenantProfile).isNotNull();
        assertThat(requestedTenantProfile.id()).isEqualTo(1);
        assertThat(requestedTenantProfile.first_name()).isEqualTo("Notenant");
        assertThat(requestedTenantProfile.middle_name()).isEqualTo("Mister");
        assertThat(requestedTenantProfile.last_name()).isEqualTo("Profile");
        assertThat(requestedTenantProfile.user_id()).isEqualTo(5);
    }

    @Test
    @DirtiesContext
    void shouldUpdateExistingTenantProfile() {
        TenantProfileRequest savedTenantProfileRequest = new TenantProfileRequest("MICHAEL", "MAN", "TENANT");

        EntityExchangeResult<TenantProfileResponse> result = clientWithBasicAuth("tenant", "pass123")
                .put()
                .uri("/api/tenants")
                .body(savedTenantProfileRequest)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TenantProfileResponse.class)
                .returnResult();

        TenantProfileResponse requestedTenantProfile = result.getResponseBody();
        assertThat(requestedTenantProfile).isNotNull();
        assertThat(requestedTenantProfile.id()).isEqualTo(11);
        assertThat(requestedTenantProfile.first_name()).isEqualTo("MICHAEL");
        assertThat(requestedTenantProfile.middle_name()).isEqualTo("MAN");
        assertThat(requestedTenantProfile.last_name()).isEqualTo("TENANT");
        assertThat(requestedTenantProfile.user_id()).isEqualTo(3);
    }

    @Test
    @DirtiesContext
    void shouldDeleteExistingTenantProfile() {
        clientWithBasicAuth("tenant", "pass123")
                .delete()
                .uri("/api/tenants")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isNoContent();

        clientWithBasicAuth("tenant", "pass123")
                .get()
                .uri("/api/tenants/me")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
