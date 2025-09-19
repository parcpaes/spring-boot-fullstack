package com.parcpaes.customer;

import com.parcpaes.exceptions.DuplicateResourceException;
import com.parcpaes.exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {


    @Mock
    private CustomerDao customerDao;

    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //given

        //when
        underTest.getAllCustomers();

        //then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void shouldGetCustomerById(){
        //given
        int id = 10;
        Customer customer = new Customer(id,"Alex","alex@gmail.com",19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //when
        var actual = underTest.getCustomer(id);

        assertNotNull(actual);
        assertThat(actual).isEqualTo(customer);
        verify(customerDao).selectCustomerById(id);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        //given
        int id = 10;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(ResourceNotFound.class, ()->underTest.getCustomer(id));

        assertThatThrownBy(()->underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage(String.format("Customer with id [%s] not found", id));
    }

    @Test
    void addCustomer() {
        //given
        int id = 10;
        String email = "alex@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistration request = new CustomerRegistration("alex",email,19);
        //when
        var actual = underTest.addCustomer(request);

        //then
        ArgumentCaptor<Customer> customerArgCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgCaptor.capture());

        Customer capturedCustomer = customerArgCaptor.getValue();

        assertThat(actual).isNull();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        //given
        String email = "alex@gmail.com";

        CustomerRegistration request = new CustomerRegistration("alex",email,19);
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);
        //then
        assertThatThrownBy(()->underTest.addCustomer(request))
            .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(String.format("Email already taken"));

        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById(){
        //given
        int id = 1;
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        //when
        underTest.deleteCustomer(id);

        //then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeleteCustomerByIdDoesNotExist() {
        final int id = -1;
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        assertThatThrownBy(()->underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage(String.format("Customer with [%s] not found", id));

        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void updateCustomer(){
        int id = 1;
        String email = "alex@gmail.com";
        Customer customer = new Customer(id, "alex", email,19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdate = new CustomerUpdateRequest(
                "Juan",
                email,
                20
        );

        //when
        underTest.updateCustomer(id, customerUpdate);
        ArgumentCaptor<Customer> customerArgCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgCaptor.capture());

        Customer capturedCustomer = customerArgCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(customerUpdate.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerUpdate.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerUpdate.age());
    }

    @Test
    void shouldThrowExceptionWhenUpdateCustomerEmailAlreadyExists() {
        //given
        int id = 1;
        String updatedEmail = "alexyo@gmail.com";
        Customer customer = new Customer(id, "alex","alex@gmail.com",19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(updatedEmail)).thenReturn(true);

        CustomerUpdateRequest customerUpdate = new CustomerUpdateRequest(
                "Juan",
                updatedEmail,
                20
        );

        //when
        assertThatThrownBy(()-> underTest.updateCustomer(id, customerUpdate))
        .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(String.format("Email already taken"));

        verify(customerDao, never()).updateCustomer(any());
    }
}