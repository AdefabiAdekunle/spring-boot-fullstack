package com.adekunle.customer;

import com.adekunle.exception.DuplicateResourceException;
import com.adekunle.exception.RequestValidationException;
import com.adekunle.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    private final PasswordEncoder passwordEncoder;

    private final CustomerDTOMapper customerDTOMapper;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }


    public List<CustomerDTO> getAllCustomers() {
        return customerDao.selectAllCustomer()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(
                        () -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id))
                        //() -> new IllegalArgumentException("customer with id [%s] not found".formatted(id))
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {

        //check if email exist
        if (customerDao.checkIfEmailExist(customerRegistrationRequest.email())) {

            throw new DuplicateResourceException("email already taken");
        }

        // add
            Customer customer = new Customer(
                    customerRegistrationRequest.name(),
                    customerRegistrationRequest.email(),
                    customerRegistrationRequest.age(),
                    customerRegistrationRequest.gender(),
                    passwordEncoder.encode(customerRegistrationRequest.password()));

            customerDao.insertCustomer(customer);

    }

    public void deleteCustomer(Integer id) {
        //check if customer exist first
        if (!customerDao.checkIfIdExist(id)) {
           throw  new ResourceNotFoundException("customer with id [%s] not found".formatted(id));
        }

        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = customerDao.selectCustomerById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id))
                        //() -> new IllegalArgumentException("customer with id [%s] not found".formatted(id))
                );
        boolean changes = false;

        if (customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customer.getName())) {
            customer.setName(customerUpdateRequest.name());
            changes = true;
        }

        if (customerUpdateRequest.age() != null && !customerUpdateRequest.age().equals(customer.getAge())) {
            customer.setAge(customerUpdateRequest.age());
            changes = true;
        }

        if (customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customer.getEmail())) {
            if (customerDao.checkIfEmailExist(customerUpdateRequest.email())) {
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            customer.setEmail(customerUpdateRequest.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);

        //OR
//        if(isAnyOrAllFieldRemainTheSame(customer , customerUpdateRequest)){
//            throw new RequestValidationException("no data changes found");
//        }
//            String email = customerUpdateRequest.email();
//            String name = customerUpdateRequest.name();
//            Integer age = customerUpdateRequest.age();
//
//            if (email != null){
//                if(customerDao.checkIfEmailExist(email) && !email.equals(customer.getEmail())) {
//                    throw new DuplicateResourceException("email already taken");
//                }
//                customer.setEmail(email);
//            }
//            if (name != null) {
//                customer.setName(name);
//            }
//
//            if( age != null){
//                customer.setAge(age);
//            }
//
//        customerDao.updateCustomer(customer);



    }

    public boolean isAnyOrAllFieldRemainTheSame(Customer customer, CustomerUpdateRequest customerUpdateRequest) {
        String email = customerUpdateRequest.email();
        String name = customerUpdateRequest.name();
        Integer age = customerUpdateRequest.age();

        //check if a single field exist and no data change exist
        if(email!= null && name == null && age == null){
            return email.equalsIgnoreCase(customer.getEmail());
        }

        else if( email== null && name != null && age == null){
            return name.equalsIgnoreCase(customer.getName());
        }

        else if( email== null && name == null && age != null){
            return age.equals(customer.getAge());
        }

        //check if two or more field exist and no data changes exist
        if( email!= null && name != null && age == null){
            return email.equals(customer.getEmail()) && name.equals(customer.getName());
        }

       else if (email != null && name == null && age != null){
            return email.equals(customer.getEmail()) && age.equals(customer.getAge());
        }
        else if (email == null && name != null && age != null){
            return name.equals(customer.getName()) && age.equals(customer.getAge());
        }

        else if (email != null && name != null && age != null){
            return email.equals(customer.getEmail()) &&
                    name.equals(customer.getName()) &&
                    age.equals(customer.getAge());
        }

        return false;
    }
}
