package com.parcpaes.customer;

import com.parcpaes.exceptions.DuplicateResourceException;
import com.parcpaes.exceptions.ResourceNotFound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
      return this.customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id){
        return customerDao.selectCustomerById(id)
                .orElseThrow(
                        ()-> new ResourceNotFound(
                                String.format("Customer with id [%s] not found", id)
                        ));
    }

    public Customer addCustomer(CustomerRegistration customerRegistration){
        verifyEmail(customerRegistration.email());

        Customer customer = new Customer(
                customerRegistration.name(),
                customerRegistration.email(),
                customerRegistration.age()
        );
        return this.customerDao.insertCustomer(customer);
    }

    private void verifyEmail(String email) {
        if(customerDao.existsCustomerWithEmail(email)){
            throw new DuplicateResourceException("Email already taken");
        }
    }

    public Customer updateCustomer(Integer id, CustomerUpdateRequest customerUpdateRequest){

        Customer customer = getCustomer(id);
        if(!customerUpdateRequest.email().equals(customer.getEmail())){
            verifyEmail(customerUpdateRequest.email());
        }

        customer.setName(customerUpdateRequest.name());
        customer.setEmail(customerUpdateRequest.email());
        customer.setAge(customerUpdateRequest.age());

        return customerDao.updateCustomer(customer);
    }

    public void deleteCustomer(Integer id){
        if(!customerDao.existsCustomerWithId(id)){
            throw new ResourceNotFound(String.format("Customer with [%s] not found", id));
        }
        this.customerDao.deleteCustomerById(id);
    }
}
