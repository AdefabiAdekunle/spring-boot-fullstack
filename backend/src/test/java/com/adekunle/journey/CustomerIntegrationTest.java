package com.adekunle.journey;

import com.adekunle.customer.*;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.apache.tomcat.util.http.parser.Authorization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "/api/v1/customers";

    @Test
    void canRegisterACustomer() {
        // create registration request
        Faker faker = new Faker();

        Name fakerName  = faker.name();
        String name  = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@foobarhello123.com";
        int age  = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,email,age, Gender.MALE,
                "password");

        // send a post request

        String jwt =webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)// it doesn't return anything yet we want the jwt token the request produce
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0); //the first element is our token

        // get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

//        Customer expectedCustomer = new Customer(
//                name, email , age, Gender.MALE,
//                "password");


        assert allCustomers != null;
        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                        .map(CustomerDTO::id)
                                .findFirst()
                                        .orElseThrow();
        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                name,
                email,
                Gender.MALE,
                age,
                List.of("ROLE_USER"),
                email
        );
        // make sure that customer is present
        assertThat(allCustomers)
                //.usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create registration request
        Faker faker = new Faker();

        Name fakerName  = faker.name();
        String name  = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@foobarhello123.com";
        int age  = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,email,age , Gender.MALE,
                "password");

        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name + "kunlexy",email + "for integration",age , Gender.MALE,
                "password");

        // NOTE we are going to post two customers and we will use the token from the second customers to check
        // if the first customer has been deleted

        // send a post request
        String jwt1 =webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)// it doesn't return anything yet we want the jwt token the request produce
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        String jwt2 =webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)// it doesn't return anything yet we want the jwt token the request produce
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        // get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt1))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();



        assert allCustomers != null;
        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        // delete the customer
        webTestClient.delete()
                        .uri(CUSTOMER_URI + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt1))
                        .exchange()
                        .expectStatus()
                        .isOk();



        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt2))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create registration request
        Faker faker = new Faker();

        Name fakerName  = faker.name();
        String name  = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@foobarhello123.com";
        int age  = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,email,age, Gender.MALE,
                "password");

        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name + "kunlex",email + "for integrationUpdate",age , Gender.MALE,
                "password");

        // NOTE we are going to post two customers and we will use the token from the second customers to check
        // if the customer has been updated

        // send a post request
        String jwt1 =webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)// it doesn't return anything yet we want the jwt token the request produce
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0); //the first element is our token

        String jwt2 =webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)// it doesn't return anything yet we want the jwt token the request produce
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0); //the first element is our token

        // get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt1))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();



        // Get the id
        assert allCustomers != null;
        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        //Create Update request
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Yusuf",fakerName.lastName() + "-" + UUID.randomUUID() + "@foobarhello123.com", 20, Gender.MALE
        );

        CustomerDTO expected = new CustomerDTO(
                id,
                customerUpdateRequest.name(),
                customerUpdateRequest.email(),
                Gender.MALE,
                customerUpdateRequest.age(),
                List.of("ROLE_USER"),
                customerUpdateRequest.email()
        );
//        Customer expected = new Customer(
//                id, customerUpdateRequest.name(),customerUpdateRequest.email(),customerUpdateRequest.age(), Gender.MALE,
//                "password");

         // Update customer data
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt1))
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get Updated customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt2))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {})
                .isEqualTo(expected);

    }
}
