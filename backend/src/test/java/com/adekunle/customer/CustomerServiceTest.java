package com.adekunle.customer;

import com.adekunle.exception.DuplicateResourceException;
import com.adekunle.exception.RequestValidationException;
import com.adekunle.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)// If we don't want to use private AutoCloseable autoCloseable;
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomer();
    }

    @Test
    void canGetCustomer() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
       Customer actual =  underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowErrorWhenGetCustomerReturnEmptyOptional() {
        //Given
        int id =10;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "customer with id [%s] not found".formatted(id)
                );
    }

    @Test
    void addCustomer() {
        //Given
        String email = "alex@gmail.com";
        when(customerDao.checkIfEmailExist(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email , 19
        );


        //When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());


    }

    @Test
    void willThrowWhenEmailExistWhileAddCustomer() {
        //Given
        String email = "alex@gmail.com";

        when(customerDao.checkIfEmailExist(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email , 19
        );
        // Then
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomer() {

        //Given
        int id = 10;
        when(customerDao.checkIfIdExist(id)).thenReturn(true);

        // when
        underTest.deleteCustomer(id);

        //then
        verify(customerDao).deleteCustomerById(id);

    }

    @Test
    void willThrowErrorWhenIdNotExist() {
        //Given
        int id = 10;
        when(customerDao.checkIfIdExist(id)).thenReturn(false);

        //then
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao,never()).deleteCustomerById(any());
    }

    @Test
    void canUpdateAllCustomerProperties() {
        //Given
        int id  = 10;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "alex1", "alex1@gmail.com" , 20
        );
        when(customerDao.checkIfEmailExist(request.email())).thenReturn(false);
        //When
        underTest.updateCustomer(10, request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // Then
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());


        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(request.email()).isEqualTo(capturedCustomer.getEmail());
        assertThat(request.name()).isEqualTo(capturedCustomer.getName());
        assertThat(request.age()).isEqualTo(capturedCustomer.getAge());



    }

    @Test
    void canNotUpdateCustomerPropertiesWhenEmailAlreadyExist() {
        //Given
        int id  = 10;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "alex1", "alex1@gmail.com" , 20
        );
        when(customerDao.checkIfEmailExist(request.email())).thenReturn(true);

        //Then
        assertThatThrownBy(() -> underTest.updateCustomer(id,request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDao, never()).updateCustomer(any());

    }

    @Test
    void canNotUpdateCustomerPropertiesWhenNoDataChanges() {
        //Given
        int id  = 10;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "alex", "alex@gmail.com" , 19
        );

        //Then
        assertThatThrownBy(() -> underTest.updateCustomer(id,request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        verify(customerDao, never()).updateCustomer(any());

    }

}