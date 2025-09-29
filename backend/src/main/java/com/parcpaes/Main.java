package com.parcpaes;

import com.github.javafaker.Faker;
import com.parcpaes.customer.Customer;
import com.parcpaes.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
            var faker = new Faker();
            var name = faker.name();
            var random = new Random();
            String firstName = name.firstName();
            String lastName = name.lastName();
            Customer customer = new Customer(
                    String.format("%s %s", firstName, lastName),
                    String.format("%s.%s@gmail.com", firstName.toLowerCase(), lastName.toLowerCase()),
                    random.nextInt(16,99)
            );

            customerRepository.save(customer);
        };
    }
}
