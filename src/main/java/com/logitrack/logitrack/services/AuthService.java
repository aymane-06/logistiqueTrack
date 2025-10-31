package com.logitrack.logitrack.services;

import com.logitrack.logitrack.Util.PaswordUtil;
import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.mapper.UserMapper;
import com.logitrack.logitrack.models.User;
import com.logitrack.logitrack.repositories.UserRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@RequestScope
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public HttpSession login(String email, String password,HttpSession session) {
        Optional<User> user= userRepository.findByEmail(email);
        if (user.isEmpty() || !PaswordUtil.verifyPassword(password, user.get().getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        UserResponseDTO userResponseDTO = userMapper.toResponseDTO(user.get());
        String ssID= session.getId();
        session.setAttribute(ssID, userResponseDTO);
        return session;

    }


    public UserResponseDTO registerUser(UserDTO userDTO) {
        boolean exists = userRepository.existsByEmail(userDTO.getEmail());
        if (exists) {
            throw new IllegalArgumentException("Email already in use");
        }
        userDTO.setPasswordHash(PaswordUtil.hashPassword(userDTO.getPasswordHash()));
        User user = userMapper.toEntity(userDTO);
        userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }
}
