package com.goit.restapiexample.auth.dto.registration;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegistrationResponse {
    private Error error;

    public enum Error {
        ok,
        userAlreadyExists,
        invalidEmail,
        invalidPassword,
        invalidName,
        invalidAge
    }

    public static RegistrationResponse success() {
        return builder().error(Error.ok).build();
    }

    public static RegistrationResponse failed(Error error) {
        return builder().error(error).build();
    }
}