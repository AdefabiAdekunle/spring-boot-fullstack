package com.adekunle.customer;

import com.adekunle.exception.DuplicateResourceException;
import com.adekunle.exception.RequestValidationException;
import com.adekunle.exception.ResourceNotFoundException;
import com.adekunle.s3.S3Buckets;
import com.adekunle.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)// If we don't want to use private AutoCloseable autoCloseable;
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;

    @Mock
    private PasswordEncoder passwordEncoder;
    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @Mock
    private S3Service s3Service;

    @Mock
    private S3Buckets s3Buckets;


    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao, passwordEncoder, customerDTOMapper, s3Service, s3Buckets);
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
                id, "alex", "alex@gmail.com", 19 , Gender.MALE,
                "password");
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
       CustomerDTO actual =  underTest.getCustomer(id);

       CustomerDTO expected = customerDTOMapper.apply(customer);

        // Then
        assertThat(actual).isEqualTo(expected);
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
                "alex", email , 19, Gender.MALE,
                "password");

        String passwordHash = "Ikeoluwa_007";
        when(passwordEncoder.encode("password")).thenReturn(passwordHash);

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
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);


    }

    @Test
    void willThrowWhenEmailExistWhileAddCustomer() {
        //Given
        String email = "alex@gmail.com";

        when(customerDao.checkIfEmailExist(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email , 19, Gender.MALE,
                "password");
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
                id, "alex", "alex@gmail.com", 19, Gender.FEMALE,
                "password");
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "alex1", "alex1@gmail.com" , 20, Gender.FEMALE
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
                id, "alex", "alex@gmail.com", 19 , Gender.MALE,
                "password");
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "alex1", "alex1@gmail.com" , 20 , Gender.MALE
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
                id, "alex", "alex@gmail.com", 19 , Gender.MALE,
                "password");
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "alex", "alex@gmail.com" , 19 , Gender.MALE
        );

        //Then
        assertThatThrownBy(() -> underTest.updateCustomer(id,request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        verify(customerDao, never()).updateCustomer(any());

    }

    @Test
    void canUploadProfileImage() {
        //Given
        int id  = 19;
        when(customerDao.checkIfIdExist(id)).thenReturn(true);

        byte[] bytes = "Hello World".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file", bytes);
        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        underTest.uploadCustomerImage(id,  multipartFile);

        // Then

        ArgumentCaptor<String> profileImageIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        // since we capture one of the argument the other must have eq
        verify(customerDao).updateCustomerProfileImageId(profileImageIdArgumentCaptor.capture(), eq(id));

        // since we capture one of the argument the others must have eq
        verify(s3Service).putObject(
                eq(bucket),
                "profile-images/%s/%s".formatted(id, profileImageIdArgumentCaptor.capture()),
                eq(bytes)
        );
    }

    @Test
    void cannotUploadProfileImageWhenCustomerDoesNotExists() {
        //Given
        int id  = 19;
        when(customerDao.checkIfIdExist(id)).thenReturn(false);
        byte[] bytes = "Hello World".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file", bytes);


        assertThatThrownBy(() -> underTest.uploadCustomerImage(id, multipartFile))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao).checkIfIdExist(id);
        verifyNoMoreInteractions(customerDao);
        verify(s3Buckets, never()).getCustomer(); // or verifyNoInteractions(s3Buckets);
        verify(s3Service, never()).putObject(any(), any(), any()); // or verifyNoInteractions(s3Service);
    }

    @Test
    void cannotUploadProfileImageWhenExceptionIsThrown() throws IOException {
        //Given
        int id  = 19;
        when(customerDao.checkIfIdExist(id)).thenReturn(true);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        assertThatThrownBy(() -> underTest.uploadCustomerImage(id,  multipartFile))
                .isInstanceOf(RuntimeException.class)
                        .hasRootCauseInstanceOf(IOException.class);

        // Then

        verify(customerDao, never()).updateCustomerProfileImageId(any(), any());

    }

    @Test
    void canDownloadProfileImage() {
        //Given
        int id  = 10;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19, Gender.FEMALE,
                "password", "2222");
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        when(s3Service.getObject(
                bucket,
                "profile-images/%s/%s".formatted(id, customer.getProfileImageId())
        )).thenReturn("image".getBytes());

        // When
        byte[] actualImage = underTest.getCustomerProfileImage(id);

        // Then
        assertThat(actualImage).isEqualTo("image".getBytes());

    }

    @Test
    void cannotDownloadProfileImageWhenCustomerDoesNotExist() {
        //Given
        int id  = 10;

//        when(customerDao.selectCustomerById(id)).thenThrow( new ResourceNotFoundException(
//                ""customer with id [%s] not found".formatted(id)
//        ));
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomerProfileImage(id))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("customer with id [%s] not found".formatted(id));

        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);

    }

    @Test
    void cannotDownloadProfileImageWhenCustomerProfileImageIsBlank() {
        // Given
        int id  = 10;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19, Gender.FEMALE,
                "password", "");
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> underTest.getCustomerProfileImage(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] profile Image not found".formatted(id));

        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);
    }

}