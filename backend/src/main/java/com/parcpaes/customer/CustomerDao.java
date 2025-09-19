package com.parcpaes.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Integer id);

    Customer insertCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    boolean existsCustomerWithId(Integer id);

    void deleteCustomerById(Integer id);

    Customer updateCustomer(Customer customer);
}
