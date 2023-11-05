package com.adekunle.auth;

import com.adekunle.customer.CustomerDTO;

public record AuthenticationResponse (
        String token,
        CustomerDTO customerDTO
){
}
