package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.mapper.UserMapper;
import com.logitrack.logitrack.models.User;
import com.logitrack.logitrack.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public String login(String email, String password) {
        // Implement authentication logic here
        // For example, verify email and password against the database
        // If valid, generate and return a JWT token or session ID
        return "mocked-auth-token";
    }


    public UserResponseDTO registerUser(UserDTO userDTO) {
        boolean exists = userRepository.existsByEmail(userDTO.getEmail());
        if (exists) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = userMapper.toEntity(userDTO);
        userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }
}
