package com.parcpaes.customer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository()
@Qualifier("jdbc")
public class CustomerJDBCAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCAccessService(
            JdbcTemplate jdbcTemplate,
            CustomerRowMapper customerRowMapper) {

        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        String sql = "SELECT * FROM customer";
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        String sql = "SELECT * FROM customer WHERE id = ?";
        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public Customer insertCustomer(Customer customer) {
        String sql = """
            INSERT INTO customer(name, email, age)
            VALUES (?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setInt(3, customer.getAge());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if(keys == null || !keys.containsKey("id")){
            throw new IllegalStateException("Failed to retrieve generated key");
        }
        int id = ((Number) keys.get("id")).intValue();
        return selectCustomerById(id).orElseThrow();
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        String sql = "SELECT * from customer where email = ?";
        Optional<Customer> customer = jdbcTemplate.query(sql, customerRowMapper, email).stream().findFirst();
        return customer.isPresent();
    }

    @Override
    public boolean existsCustomerWithId(Integer id) {
        return selectCustomerById(id).isPresent();
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        String sql = "UPDATE customer SET name=?, email=?, age=? WHERE id = ?";
        int id = customer.getId();
        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge(), id);
        return selectCustomerById(id).orElseThrow();
    }

    @Override
    public void deleteCustomerById(Integer id) {
        String sql = "DELETE FROM customer where id = ?";
        jdbcTemplate.update(sql,id);
    }
}
