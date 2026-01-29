package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.mapper.UserMapper;
import com.logitrack.logitrack.models.ENUM.Role;
import com.logitrack.logitrack.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u->u.getRole()!= Role.ADMIN)
                .map(userMapper::toResponseDTO)
                .toList();
    }

    public UserResponseDTO getUserById(java.util.UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public UserResponseDTO updateUser(UUID id , UserDTO userDTO) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userMapper.updateUserFromDto(userDTO, user);
        userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO deleteUser(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
        return userMapper.toResponseDTO(user);
    }

}
