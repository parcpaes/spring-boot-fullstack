package com.parcpaes.customer;

import java.util.Objects;

import jakarta.persistence.*;
import org.hibernate.mapping.Constraint;

@Entity(name="Customer")
@Table(
        name="customer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name="customer_email_unique",
                        columnNames = "email"
                )
        }
)
public class Customer{
    @Id
    @SequenceGenerator(
        name="customer_id_seq",
        sequenceName = "customer_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "customer_id_seq"
    )
    @Column(
            name="id",
            nullable = false
    )
    private Integer id;

    @Column(
            name="name",
            nullable = false
    )
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(
            name="age",
            nullable = false
    )
    private int age;

    public Customer(Integer id, String name, String email, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Customer(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Customer() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return age == customer.age && Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}