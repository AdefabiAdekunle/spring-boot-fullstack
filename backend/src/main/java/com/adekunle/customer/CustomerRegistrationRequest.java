package com.adekunle.customer;

public record CustomerRegistrationRequest (
        String name,
        String email,
        Integer age
) {
}
