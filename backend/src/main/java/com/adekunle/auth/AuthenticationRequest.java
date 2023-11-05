package com.adekunle.auth;

public record AuthenticationRequest (
        String username,
        String password
){
}
