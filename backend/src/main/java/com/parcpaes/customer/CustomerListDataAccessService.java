package com.parcpaes.customer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Qualifier("list")
public class CustomerListDataAccessService implements CustomerDao{

    private static final List<Customer> customers;
    static{
        customers = new ArrayList<>();
        Customer alex = new Customer(1,
                "Alex",
                "alex@gmail.com",
                32
        );
        customers.add(alex);
        Customer jamila = new Customer(2,
                "Jamila",
                "jamila@gmail.com",
                23
        );
        customers.add(jamila);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customers.stream()
                .filter(customer-> Objects.equals(customer.getId(), id))
                .findFirst();
    }

    @Override
    public Customer insertCustomer(Customer customer) {
        customer.setId(customers.size()+1);
        customers.add(customer);
        return customer;
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream().anyMatch(
                (customer -> customer.getEmail().equals(email))
        );
    }

    @Override
    public boolean existsCustomerWithId(Integer id) {
        return customers.stream().anyMatch(
                (customer -> customer.getId().equals(id))
        );
    }

    @Override
    public void deleteCustomerById(Integer id) {
        customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        customer.setId(customers.size()+1);
        customers.add(customer);
        return customer;
    }
}
