package com.dem_ams;

import com.dem_ams.constant.ApplicationConstants;
import com.dem_ams.domain.dto.LoginRequest;
import com.dem_ams.domain.dto.LoginResponse;
import com.dem_ams.domain.dto.RegisterRequest;
import com.dem_ams.domain.dto.RegisterResponse;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.Role;
import com.dem_ams.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DemAmsAuthTests {

    @Autowired
    private RestTestClient client;

    @Autowired
    private RoleRepository roleRepository;

    private RestTestClient clientWithBasicAuth(String username, String password) {
        return client.mutate()
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    @Test
    void shouldLogin() {
        LoginRequest loginRequest = new LoginRequest("admin", "password");

        EntityExchangeResult<LoginResponse> result = clientWithBasicAuth(loginRequest.username(), loginRequest.password())
                .post()
                .uri("/api/auth/login")
                .body(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult();

        LoginResponse response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.message()).isNotNull();
        assertThat(response.user()).isNotNull();
        assertThat(response.user().role()).isEqualTo("ROLE_ADMIN");
        assertThat(response.user().user_id()).isEqualTo(10);
    }

    @Test
    void shouldLogout() {
        clientWithBasicAuth("admin", "password")
                .post()
                .uri("/api/auth/logout")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DirtiesContext
    void shouldRegisterUser() {
        RegisterRequest registerRequest = new RegisterRequest("newuser", "new@example.com", "password");
        Role role = roleRepository.findByName(ApplicationConstants.ROLE_TENANT)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find role", ApplicationConstants.ROLE_TENANT));

        EntityExchangeResult<RegisterResponse> result = client
                .post()
                .uri("/api/auth/register")
                .body(registerRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RegisterResponse.class)
                .returnResult();

        RegisterResponse response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo("newuser");
        assertThat(response.email()).isEqualTo("new@example.com");
        assertThat(response.roles()).isEqualTo(Set.of(role));
        assertThat(response.created_at()).isNotNull();
    }
}
