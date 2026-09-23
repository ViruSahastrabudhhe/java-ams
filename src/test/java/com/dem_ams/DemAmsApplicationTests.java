package com.dem_ams;

import com.dem_ams.domain.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class DemAmsApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	RestTestClient client;

	private RestTestClient clientWithBasicAuth(String username, String password) {
		return client.mutate()
				.defaultHeaders(headers -> headers.setBasicAuth(username, password))
				.build();
	}

	@Test
	void shouldReturnUsername() {
		EntityExchangeResult<String> result = clientWithBasicAuth("admin", "password")
				.get()
				.uri("/api/users/username")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult();

		String username = result.getResponseBody();
		assertThat(username).isEqualTo("admin");

		Authentication principal = new UsernamePasswordAuthenticationToken("admin", "password", AuthorityUtils.NO_AUTHORITIES);
		assertThat(username).isEqualTo(principal.getName());
	}
}
