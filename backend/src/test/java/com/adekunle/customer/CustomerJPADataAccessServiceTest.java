package com.adekunle.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.assertThat;


class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private  CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomer() {
        //before pagination
//        //When
//        underTest.selectAllCustomer();
//
//        //Then
//        verify(customerRepository).findAll();


        //after pagination
        Page<Customer> page = mock(Page.class);
        List<Customer> customers = List.of(new Customer());
        when(page.getContent()).thenReturn(customers);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
        // When
        List<Customer> expected = underTest.selectAllCustomer();

        // Then
        assertThat(expected).isEqualTo(customers);
        ArgumentCaptor<Pageable> pageArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerRepository).findAll(pageArgumentCaptor.capture());
        assertThat(pageArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(1000));
    }

    @Test
    void selectCustomerById() {
        // Given
        int id = 1;

        // When
        underTest.selectCustomerById(id);

        // Then
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        // Given
        Customer customer = new Customer(
                "adekunle",
                "adekunle@gmail.com",
                40,
                Gender.MALE,
                "password");

        // When
        underTest.insertCustomer(customer);

        // Then
        verify(customerRepository).save(customer);
    }

    @Test
    void checkIfEmailExist() {
        // Given
        String email = "adekunle@gmail.com";

        // When
        underTest.checkIfEmailExist(email);

        // Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 1;

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void checkIfIdExist() {
        // Given
        int id = 1;

        // When
        underTest.checkIfIdExist(id);

        // Then
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void updateCustomer() {
        // Given
        Customer customer = new Customer(
                1,
                "adekunle",
                "adekunle@gmail.com",
                40,
                Gender.MALE,
                "password");

        // When
        underTest.updateCustomer(customer);

        // Then
        verify(customerRepository).save(customer);
    }

    @Test
    void selectUserByEmail() {
        // Given
        String email = "adekunle@gmail.com";

        // When
        underTest.selectUserByEmail(email);

        // Then
        verify(customerRepository).findCustomersByEmail(email);
    }
    @Test
    void updateCustomerProfileImageId() {
        Customer customer = new Customer(
                1,
                "adekunle",
                "adekunle@gmail.com",
                40,
                Gender.MALE,
                "password");

        underTest.updateCustomerProfileImageId("2222",customer.getId());

        verify(customerRepository).updateProfileImageId("2222",customer.getId());

    }

}