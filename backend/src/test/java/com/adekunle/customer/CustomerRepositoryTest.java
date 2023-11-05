package com.adekunle.customer;

import com.adekunle.AbstractTestContainersUnitTest;
import com.adekunle.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
class CustomerRepositoryTest extends AbstractTestContainersUnitTest {


    @Autowired private CustomerRepository underTest;


    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void existsCustomerByEmail() {
        //Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer1 = new Customer(
                FAKER.name().fullName(),
                email,
                20,
                Gender.FEMALE,
                "password");

        underTest.save(customer1);

        //When
        Boolean actual = underTest.existsCustomerByEmail(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerById() {
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                FAKER.internet().emailAddress() + "-" + UUID.randomUUID(),
                40,
                Gender.MALE,
                "password");
        underTest.save(customer);
        Integer id = underTest.findAll()
                .stream().filter(customer1 -> customer1.getName().equalsIgnoreCase(name))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        Boolean actual = underTest.existsCustomerById(id);

        //Then
        assertThat(actual).isTrue();
    }
}