package com.adekunle.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface CustomerDao {
    List<Customer> selectAllCustomer();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean checkIfEmailExist(String email);
    void deleteCustomerById(Integer id);
    boolean checkIfIdExist(Integer id);
    void updateCustomer( Customer request);
    Optional<Customer> selectUserByEmail(String email);
    void updateCustomerProfileImageId(String profileImageId, Integer customerId);
}
