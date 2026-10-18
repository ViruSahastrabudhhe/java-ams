package com.dem_ams;

import com.dem_ams.domain.model.UserModel;
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
class DemAmsApplicationTests {

	@Autowired
	RestTestClient client;

	private RestTestClient clientWithBasicAuth(String username, String password) {
		return client.mutate()
				.defaultHeaders(headers -> headers.setBasicAuth(username, password))
				.build();
	}

	@Test
	void shouldReturnExistingUser() {
		EntityExchangeResult<UserModel> response = clientWithBasicAuth("admin", "password")
				.get()
				.uri("/api/users/10")
				.accept(MediaTypes.HAL_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(UserModel.class)
				.returnResult();

		UserModel userModel = response.getResponseBody();
		assertThat(userModel).isNotNull();
		assertThat(userModel.getUsername()).isEqualTo("admin");
		assertThat(userModel.getPassword()).isEqualTo("{noop}password");
		assertThat(userModel.getId()).isEqualTo(10);

		Authentication principal = new UsernamePasswordAuthenticationToken("admin", "password", AuthorityUtils.NO_AUTHORITIES);
		assertThat(userModel.getUsername()).isEqualTo(principal.getName());
	}

	@Test
	void shouldNotReturnNonExistentUser() {
		clientWithBasicAuth("admin", "password")
				.get()
				.uri("/api/users/888/info")
				.accept(MediaTypes.HAL_JSON)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void shouldNotAllowAccessToUserTheyDoNotOwn() {
		clientWithBasicAuth("tenant", "pass123")
				.get()
				.uri("/api/users/4")
				.accept(MediaTypes.HAL_JSON)
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	void shouldReturnOkWhenAuthenticatedAccessingPublic() {
		clientWithBasicAuth("admin", "password")
				.get()
				.uri("/api/public")
				.accept(MediaTypes.HAL_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class);
	}

	@Test
	@DirtiesContext
	void shouldUpdateExistingUser() {
		UserModel updatedUserModel = new UserModel();
		updatedUserModel.setUsername("jejomar");
		updatedUserModel.setEmail("jejomar@example.com");
		updatedUserModel.setPassword("{noop}password");

		clientWithBasicAuth("admin", "password")
				.put()
				.uri("/api/users/10")
				.body(updatedUserModel)
				.exchange()
				.expectStatus().isNoContent()
				.expectBody(UserModel.class)
				.returnResult();

		EntityExchangeResult<UserModel> response = clientWithBasicAuth("jejomar", "password")
				.get()
				.uri("/api/users/10")
				.accept(MediaTypes.HAL_JSON)
				.exchange()
				.expectBody(UserModel.class)
				.returnResult();

		UserModel getUserModel = response.getResponseBody();
		assertThat(getUserModel).isNotNull();
		assertThat(getUserModel.getId()).isEqualTo(10);
		assertThat(getUserModel.getEmail()).isEqualTo("jejomar@example.com");
		assertThat(getUserModel.getUsername()).isEqualTo("jejomar");
		assertThat(getUserModel.getPassword()).isEqualTo("{noop}password");
		assertThat(getUserModel.getUpdatedAt()).isNotNull();

		Authentication principal = new UsernamePasswordAuthenticationToken("jejomar", "password", AuthorityUtils.NO_AUTHORITIES);
		assertThat(getUserModel.getUsername()).isEqualTo(principal.getName());
	}
}
