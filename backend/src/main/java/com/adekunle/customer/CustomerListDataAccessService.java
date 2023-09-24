package com.adekunle.customer;

import com.adekunle.exception.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao{

    // db
    private static final List<Customer> customers;

    static {
        customers = new ArrayList<>();
        Customer alex = new Customer(1,"Alex","alex@gmail.com",21);
        Customer jamila = new Customer(2,"jamila","jamila@gmail.com",19);
        customers.add(alex);
        customers.add(jamila);

    }
    @Override
    public List<Customer> selectAllCustomer() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customers.stream().filter(customer -> customer.getId().equals(id))
                .findFirst();
    }
    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }
    @Override
    public boolean checkIfEmailExist(String email) {
        return customers.stream()
                .anyMatch(customer -> customer.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public void deleteCustomerById(Integer id) {
//        Customer customer = selectCustomerById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id)));
//        customers.remove(customer);
        selectCustomerById(id)
                .ifPresent(customers::remove);
    }

    @Override
    public boolean checkIfIdExist(Integer id) {
        return customers.stream()
                .anyMatch(customer -> customer.getId().equals(id));
    }

    @Override
    public void updateCustomer(Customer request) {
        customers.add(request);
    }
}
