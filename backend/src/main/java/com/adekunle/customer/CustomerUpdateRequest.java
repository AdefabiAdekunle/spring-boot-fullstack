package com.adekunle.customer;

public record CustomerUpdateRequest (
        String name,
        String email,
        Integer age
) {
}
