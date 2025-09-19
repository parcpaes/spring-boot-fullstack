package com.parcpaes.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping()
    public List<Customer> getCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public Customer getCustomer(@PathVariable("customerId") Integer id){
        return customerService.getCustomer(id);
    }

    @PostMapping
    public ResponseEntity<Customer> insertCustomer(@RequestBody CustomerRegistration customer){
        return new ResponseEntity<>(customerService.addCustomer(customer), HttpStatus.CREATED);
    }

    @PutMapping("{customerId}")
    public ResponseEntity<Customer> updateCustomer(
            @RequestBody CustomerUpdateRequest customer,
            @PathVariable("customerId") Integer id){

        return new ResponseEntity<>(customerService.updateCustomer(id, customer), HttpStatus.OK);
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("customerId") Integer id){
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
