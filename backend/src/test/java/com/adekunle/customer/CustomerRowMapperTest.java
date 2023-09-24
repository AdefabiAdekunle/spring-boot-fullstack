package com.adekunle.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {


    private CustomerRowMapper underTest;

    @Mock
    private ResultSet rs;

    @BeforeEach
    void setUp() {
        underTest = new CustomerRowMapper();
    }

    @Test
    void mapRow() throws SQLException {
        when(rs.getInt("id")).thenReturn(123);
        when(rs.getString("name")).thenReturn("John Doe");
        when(rs.getString("email")).thenReturn("johndoe@example.com");
        when(rs.getInt("age")).thenReturn(30);

        Customer customer = underTest.mapRow(rs , 1);

        assertThat(123).isEqualTo(customer.getId());
        assertThat("John Doe").isEqualTo(customer.getName());
        assertThat("johndoe@example.com").isEqualTo(customer.getEmail());
        assertThat(30).isEqualTo(customer.getAge());


    }

}