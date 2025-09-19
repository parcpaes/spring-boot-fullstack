package com.parcpaes.customer;

import com.parcpaes.AbstractTestcontainersTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.shaded.com.google.errorprone.annotations.FormatString;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


class CustomerJDBCAccessServiceTest extends AbstractTestcontainersTest {

    private CustomerJDBCAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //given

        Customer customer = new Customer(
                FAKER.name().fullName(),
                String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID()),
                20
        );
        underTest.insertCustomer(customer);
        //when
        List<Customer> customers = underTest.selectAllCustomers();

        //then
        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById(){
        //given
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c->c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        customer.setId(id);
        //when
        var actual = underTest.selectCustomerById(id);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual).isPresent().hasValueSatisfying(c->{
            assertEquals(c, customer);
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById(){
        //given
        int id = -1;

        //when
        var actual = underTest.selectCustomerById(id);

        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer(){
        //given
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        //when
        var actual = underTest.insertCustomer(customer);
        customer.setId(actual.getId());

        //then
        assertThat(actual).isEqualTo(customer);

    }

    @Test
    void existsPersonWithEmail(){
        //given
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        //when
        boolean actual = underTest.existsCustomerWithEmail(email);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existPersonWithEmailReturnsFalseWhenDoesNotExist(){
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());

        //when
        boolean actual = underTest.existsCustomerWithEmail(email);

        //then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId(){
        //given
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        var storedCustomer = underTest.insertCustomer(customer);

        //when
        boolean actual = underTest.existsCustomerWithId(storedCustomer.getId());

        //then
        assertThat(storedCustomer).isNotNull();
        assertThat(actual).isTrue();
    }

    @Test
    void deleteCustomerById(){
        //given
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        var storedCustomer = underTest.insertCustomer(customer);

        //when
        underTest.deleteCustomerById(storedCustomer.getId());
        boolean actual = underTest.existsCustomerWithId(storedCustomer.getId());
        assertThat(actual).isFalse();
    }

    @Test
    void updateCustomer(){
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        var storedCustomer = underTest.insertCustomer(customer);

        //when
        Customer updatedCustomer = new Customer(
                FAKER.name().fullName(),
                email,
                25
        );
        updatedCustomer.setId(storedCustomer.getId());
        var actual = underTest.updateCustomer(updatedCustomer);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(updatedCustomer);
    }
}