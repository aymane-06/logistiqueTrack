package com.logitrack.logitrack.dtos.Auth;

import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private String message;
    private UserResponseDTO  user;
}