package com.goit.restapiexample.auth;

import com.goit.restapiexample.auth.dto.login.LoginRequest;
import com.goit.restapiexample.auth.dto.login.LoginResponse;
import com.goit.restapiexample.auth.dto.registration.RegistrationRequest;
import com.goit.restapiexample.auth.dto.registration.RegistrationResponse;
import com.goit.restapiexample.security.JwtUtil;
import com.goit.restapiexample.users.User;
import com.goit.restapiexample.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final int MAX_USER_ID_LENGTH = 100;
    private static final int MAX_PASSWORD_LENGTH = 255;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_AGE = 100;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public RegistrationResponse register(RegistrationRequest request) {
        User existingUser = userService.findByUsername(request.getEmail());

        if (Objects.nonNull(existingUser)) {
            return RegistrationResponse.failed(RegistrationResponse.Error.userAlreadyExists);
        }

        Optional<RegistrationResponse.Error> validationError = validateRegistrationFields(request);

        if (validationError.isPresent()) {
            return RegistrationResponse.failed(validationError.get());
        }

        userService.saveUser(User.builder()
                .userId(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .age(request.getAge())
                .build());

        return RegistrationResponse.success();
    }

    public LoginResponse login(LoginRequest request) {
        Optional<LoginResponse.Error> validationError = validateLoginFields(request);

        if (validationError.isPresent()) {
            return LoginResponse.failed(validationError.get());
        }

        User user = userService.findByUsername(request.getEmail());

        if (Objects.isNull(user)) {
            return LoginResponse.failed(LoginResponse.Error.invalidEmail);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return LoginResponse.failed(LoginResponse.Error.invalidPassword);
        }

        String authToken = jwtUtil.generateToken(request.getEmail());

        return LoginResponse.success(authToken);
    }

    private Optional<RegistrationResponse.Error> validateRegistrationFields(RegistrationRequest request) {
        if (Objects.isNull(request.getEmail()) || request.getEmail().length() > MAX_USER_ID_LENGTH) {
            return Optional.of(RegistrationResponse.Error.invalidEmail);
        }

        if (Objects.isNull(request.getPassword()) || request.getPassword().length() > MAX_PASSWORD_LENGTH) {
            return Optional.of(RegistrationResponse.Error.invalidPassword);
        }

        if (Objects.isNull(request.getName()) || request.getName().length() > MAX_USER_ID_LENGTH) {
            return Optional.of(RegistrationResponse.Error.invalidName);
        }

        if (Objects.isNull(request.getAge()) || request.getAge() > MAX_AGE) {
            return Optional.of(RegistrationResponse.Error.invalidAge);
        }

        return Optional.empty();
    }

    private Optional<LoginResponse.Error> validateLoginFields(LoginRequest request) {
        if (Objects.isNull(request.getEmail()) || request.getEmail().length() > MAX_USER_ID_LENGTH) {
            return Optional.of(LoginResponse.Error.invalidEmail);
        }

        if (Objects.isNull(request.getPassword()) || request.getPassword().length() > MAX_PASSWORD_LENGTH) {
            return Optional.of(LoginResponse.Error.invalidPassword);
        }

        return Optional.empty();
    }
}