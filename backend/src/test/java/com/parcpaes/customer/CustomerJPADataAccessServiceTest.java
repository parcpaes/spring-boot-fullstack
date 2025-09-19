package com.parcpaes.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    private static Faker FAKER = new Faker();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers(){
        //when
        underTest.selectAllCustomers();

        //then
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById(){
        //given
        int id = 1;

        //when
        underTest.selectCustomerById(id);

        //then
        verify(customerRepository).findById(id);
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
        underTest.insertCustomer(customer);

        //then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail(){
        //given
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());

        //when
        underTest.existsCustomerWithEmail(email);

        //then
        verify(customerRepository).existsByEmail(email);
    }


    @Test
    void deleteCustomerById(){
        //given
        int id = 1;

        //when
        underTest.deleteCustomerById(id);

        //then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer(){
        //given
        String email = String.format("%s-%s", FAKER.internet().emailAddress(), UUID.randomUUID());
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        //when
        underTest.updateCustomer(customer);

        //then
        verify(customerRepository).save(customer);
    }
}