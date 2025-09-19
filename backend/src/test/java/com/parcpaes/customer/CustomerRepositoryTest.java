package com.parcpaes.customer;

import com.parcpaes.AbstractTestcontainersTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainersTest {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void existsCustomerByEmail() {
        //given
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.save(customer);

        //when
        var actual = underTest.existsByEmail(email);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailNotExists() {
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());

        //when
        var actual = underTest.existsByEmail(email);

        //then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById(){
        //given
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        var savedCustomer = underTest.save(customer);
        //when
        var actual = underTest.existsCustomerById(savedCustomer.getId());

        //then
        assertThat(actual).isNotNull();
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdFailsWhenIdNotExists() {
        //given
        int id=-1;

        //when
        var actual = underTest.existsCustomerById(id);

        //then
        assertThat(actual).isFalse();
    }
}