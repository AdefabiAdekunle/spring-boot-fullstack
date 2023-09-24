package com.adekunle.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomer() {
        var sql = """
                SELECT * FROM customer;
                """;

//        RowMapper<Customer> rowMapper = (rs , rowNum)-> {
//            Customer customer = new Customer(
//                    rs.getInt("id"),
//                    rs.getString("name"),
//                    rs.getString("email"),
//                    rs.getInt("age")
//            );
//            return customer;
//        };

//        List<Customer> customers = jdbcTemplate.query(sql , (rs , rowNum)-> {
//            Customer customer = new Customer(
//                    rs.getInt("id"),
//                    rs.getString("name"),
//                    rs.getString("email"),
//                    rs.getInt("age")
//            );
//            return customer;
//        });

        List<Customer> customers = jdbcTemplate.query(sql , customerRowMapper);
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
               SELECT * FROM customer where id = ?;
                """;
       return jdbcTemplate.query(sql,customerRowMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ? , ?)
                """;
        int update = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );
        System.out.println("jdbcTemplate.update = "+ update);
    }

    @Override
    public boolean checkIfEmailExist(String email) {

        var sql = """
                SELECT count(id)
                FROM customer where email = ?
                """;
       // return false;

       Integer count =  jdbcTemplate.queryForObject(sql, Integer.class , email);

       return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                DELETE FROM customer
                WHERE id = ?
                """;
        jdbcTemplate.update(sql,id);
    }

    @Override
    public boolean checkIfIdExist(Integer id) {
        var sql = """
                SELECT count(id)
                FROM customer where id = ?
                """;
        Integer count =  jdbcTemplate.queryForObject(sql, Integer.class , id);

        return  count != null && count > 0;
    }

    @Override
    public void updateCustomer(Customer request) {
        var sql = """
    UPDATE customer SET name = ?, email = ?, age = ? WHERE id = ?
              """;
     int result = jdbcTemplate.update(sql, request.getName(), request.getEmail(), request.getAge(), request.getId());
        System.out.println("update customer result = " + result);

     //OR
//        if (request.getName() != null) {
//            String sql = "UPDATE customer SET name = ? WHERE id = ?";
//            int result = jdbcTemplate.update(
//                    sql,
//                    request.getName(),
//                    request.getId()
//            );
//            System.out.println("update customer name result = " + result);
//        }
//        if (request.getAge() != null) {
//            String sql = "UPDATE customer SET age = ? WHERE id = ?";
//            int result = jdbcTemplate.update(
//                    sql,
//                    request.getAge(),
//                    request.getId()
//            );
//            System.out.println("update customer age result = " + result);
//        }
//        if (request.getEmail() != null) {
//            String sql = "UPDATE customer SET email = ? WHERE id = ?";
//            int result = jdbcTemplate.update(
//                    sql,
//                    request.getEmail(),
//                    request.getId());
//            System.out.println("update customer email result = " + result);
//        }

    }
}
