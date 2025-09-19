package com.parcpaes.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.parcpaes.customer.Customer;
import com.parcpaes.customer.CustomerRegistration;
import com.parcpaes.customer.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Range;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();
    private static final String customerURI = "api/v1/customers";
    @Test
    void shouldRegisterCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + UUID.randomUUID() +"@foobar.com";
        int age = RANDOM.nextInt(1, 100);
        CustomerRegistration request = new CustomerRegistration(
                name, email, age
        );
        //send a post request

        webTestClient.post()
            .uri(customerURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isCreated();
        //get  all customers
        List<Customer> allCustomer = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present
        Customer expectedCustomer = new Customer(
                name, email, age
        );
        assertThat(allCustomer)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        //get customer by id
        int id = allCustomer.stream()
                .filter(c-> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);
        webTestClient.get()
                .uri(customerURI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }

    @Test
    void shouldDeleteCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + UUID.randomUUID() +"@foobar.com";
        int age = RANDOM.nextInt(1, 100);
        CustomerRegistration request = new CustomerRegistration(
                name, email, age
        );

        //send a post request

        webTestClient.post()
                .uri(customerURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isCreated();
        //get  all customers
        List<Customer> allCustomer = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        int id = allCustomer.stream()
                .filter(c-> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //delete customer
        webTestClient.delete()
                .uri(customerURI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();

        //get customer by id
        webTestClient.get()
                .uri(customerURI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void shouldUpdateCustomer() {
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + UUID.randomUUID() +"@foobar.com";
        int age = RANDOM.nextInt(1, 100);
        CustomerRegistration request = new CustomerRegistration(
                name, email, age
        );

        //send a post request
        webTestClient.post()
                .uri(customerURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isCreated();
        //get  all customers
        List<Customer> allCustomer = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        int id = allCustomer.stream()
                .filter(c-> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //update customer
        String updatedEmail = "alex" + UUID.randomUUID() +"@gmail.com";
        String updatedName = "New Alex";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                 updatedName, updatedEmail, age
        );

        webTestClient.put()
                .uri(customerURI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        Customer updatedCustomer = webTestClient.get()
                .uri(customerURI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id, updatedName, updatedEmail, age
        );

        assertThat(updatedCustomer).isEqualTo(expected);
    }
}
