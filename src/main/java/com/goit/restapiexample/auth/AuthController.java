package com.goit.restapiexample.auth;

import com.goit.restapiexample.auth.dto.login.LoginRequest;
import com.goit.restapiexample.auth.dto.login.LoginResponse;
import com.goit.restapiexample.auth.dto.registration.RegistrationRequest;
import com.goit.restapiexample.auth.dto.registration.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody RegistrationRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse register(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}