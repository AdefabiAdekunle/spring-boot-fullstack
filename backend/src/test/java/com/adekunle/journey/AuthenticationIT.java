package com.adekunle.journey;

import com.adekunle.auth.AuthenticationRequest;
import com.adekunle.auth.AuthenticationResponse;
import com.adekunle.customer.CustomerDTO;
import com.adekunle.customer.CustomerRegistrationRequest;
import com.adekunle.customer.Gender;
import com.adekunle.jwt.JWTUtil;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIT {

    @Autowired
    private WebTestClient webTestClient;


    private  static final Random RANDOM = new Random();
    private static final String AUTHENTICATION_PATH = "/api/v1/auth";

    private static final String CUSTOMER_PATH = "/api/v1/customers";
    private static final JWTUtil jwtU = new JWTUtil();


    @Test
    void canLogin() {
        // create registration request
        Faker faker = new Faker();

        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@foobarhello123.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, Gender.MALE,
                "password");

        AuthenticationRequest request2 = new AuthenticationRequest(
                request.email(),
                request.password()
        );

        // send a post request
         webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

         //send a login
        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {})
                .returnResult();

        String jwtToken = result.getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        System.out.println("The token is: " +jwtToken);

        AuthenticationResponse authenticationResponse = result.getResponseBody();
        System.out.println(" response " + authenticationResponse);

        assert authenticationResponse != null;
        assertThat(jwtU.isTokenValid(jwtToken, authenticationResponse.customerDTO().username())).isTrue();

        assertThat(authenticationResponse.customerDTO().email()).isEqualTo(email);
    }
}
