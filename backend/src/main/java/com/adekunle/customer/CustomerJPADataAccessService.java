package com.adekunle.customer;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
public class CustomerJPADataAccessService implements CustomerDao {

    private final CustomerRepository customerRepository;

    public CustomerJPADataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomer() {
        return customerRepository.findAll(Pageable.ofSize(1000)).getContent();
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    @Override
    public void insertCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public boolean checkIfEmailExist(String email) {
        return customerRepository.existsCustomerByEmail(email);
    }

    @Override
    public void deleteCustomerById(Integer id) {
        customerRepository.deleteById(id);
    }

    @Override
    public boolean checkIfIdExist(Integer id) {
        return customerRepository.existsCustomerById(id);
    }

    @Override
    public void updateCustomer(Customer request) {
        customerRepository.save(request);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customerRepository.findCustomersByEmail(email);
    }

    @Override
    public void updateCustomerProfileImageId(String profileImageId, Integer customerId) {
        customerRepository.updateProfileImageId(profileImageId, customerId);
    }
}
