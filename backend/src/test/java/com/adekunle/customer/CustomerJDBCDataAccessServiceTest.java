package com.adekunle.customer;

import com.adekunle.AbstractTestContainersUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainersUnitTest {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new  CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "-" + UUID.randomUUID(),
                20,
                Gender.MALE,
                "password");
        underTest.insertCustomer(customer);


        //When
        List<Customer> customerList = underTest.selectAllCustomer();

        //Then
        assertThat(customerList.isEmpty()).isFalse();
    }

    @Test
    void selectCustomerById() {
        //Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer1 = new Customer(
                FAKER.name().fullName(),
                email,
                20,
                Gender.MALE,
                "password");

        underTest.insertCustomer(customer1);
        Integer id = underTest.selectAllCustomer()
                .stream().filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        Customer actual = underTest.selectCustomerById(id).orElseThrow();

        //Then
        assertThat(actual.getEmail()).isEqualTo(email);
        assertThat(actual.getAge()).isEqualTo(customer1.getAge());
        assertThat(actual.getId()).isEqualTo(id);

    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        //Given
        int id = -1;


        //when
        var actual = underTest.selectCustomerById(id);

        //then
        assertThat(actual).isEmpty();

    }

    @Test
    void insertCustomer() {
        //Given
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                FAKER.internet().emailAddress() + "-" + UUID.randomUUID(),
                40,
                Gender.MALE,
                "password");



        //When
        underTest.insertCustomer(customer);
        Integer age = underTest.selectAllCustomer()
                .stream().filter(customer1 -> customer1.getName().equalsIgnoreCase(name))
                .map(Customer::getAge)
                .findFirst()
                .orElseThrow();


        //Then
        assertThat(age).isEqualTo(customer.getAge());

    }

    @Test
    void checkIfEmailExist() {
        //Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer1 = new Customer(
                FAKER.name().fullName(),
                email,
                20,
                Gender.FEMALE,
                "password");

        underTest.insertCustomer(customer1);

        //When
        Boolean actual = underTest.checkIfEmailExist(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCustomerEmailDoesNotExist() {
        //Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();

        //When
        boolean actual = underTest.checkIfEmailExist(email);

        //Then
        assertThat(actual).isFalse();

    }

    @Test
    void deleteCustomerById() {
        //Given
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                FAKER.internet().emailAddress() + "-" + UUID.randomUUID(),
                40,
                Gender.MALE,
                "password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomer()
                .stream().filter(customer1 -> customer1.getName().equalsIgnoreCase(name))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //When
        underTest.deleteCustomerById(id);
        Boolean actual = underTest.checkIfIdExist(id);

        //Then
        assertThat(actual).isFalse();

    }


    @Test
    void checkIfIdExist() {
        //Given
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                FAKER.internet().emailAddress() + "-" + UUID.randomUUID(),
                40,
                Gender.MALE,
                "password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomer()
                .stream().filter(customer1 -> customer1.getName().equalsIgnoreCase(name))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        Boolean actual = underTest.checkIfIdExist(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCustomerIdDoesNotExist() {

        //Given
        Integer id = 50;

        //When
        Boolean actual = underTest.checkIfIdExist(id);

        //Then
        assertThat(actual).isFalse();

    }

    @Test
    void updateCustomer() {
        //Given
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                FAKER.internet().emailAddress() + "-" + UUID.randomUUID(),
                40,
                Gender.FEMALE,
                "password");
        underTest.insertCustomer(customer);
        Customer customer1 = underTest.selectAllCustomer().stream()
                .filter( c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow();
        String name1 = FAKER.name().fullName();
        customer1.setName(name1);

        int requestId = customer1.getId();

        underTest.updateCustomer(customer1);

        //when
        Customer customer2 = underTest.selectAllCustomer().stream()
                .filter( c -> c.getName().equals(name1))
                .findFirst()
                .orElseThrow();

        //Then
        assertThat(customer2.getName()).isEqualTo(name1);
        assertThat(customer2.getId()).isEqualTo(requestId);
    }

    @Test
    void selectUserByEmail() {
        //Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                40,
                Gender.MALE,
                "password");
        underTest.insertCustomer(customer);

        //When
        Optional<Customer> actual = underTest.selectUserByEmail(email);

        //Then
        assertThat(actual).isPresent()
                .hasValueSatisfying(customer1 -> assertThat(customer1.getEmail()).isEqualTo(email));
    }

    @Test
    void updateCustomerProfileImageId() {
        //Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                40,
                Gender.MALE,
                "password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomer()
                .stream().filter(customer1 -> customer1.getName().equalsIgnoreCase(name))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //when
        underTest.updateCustomerProfileImageId("2222",id);

        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent()
                .hasValueSatisfying(c -> assertThat(c.getProfileImageId()).isEqualTo("2222"));

    }
}