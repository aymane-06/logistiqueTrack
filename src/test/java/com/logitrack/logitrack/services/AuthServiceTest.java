package com.logitrack.logitrack.services;

import com.logitrack.logitrack.Util.PaswordUtil;
import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.mapper.UserMapper;
import com.logitrack.logitrack.models.User;
import com.logitrack.logitrack.models.Client;
import com.logitrack.logitrack.models.Admin;
import com.logitrack.logitrack.models.WAREHOUSE_MANAGER;
import com.logitrack.logitrack.models.ENUM.Role;
import com.logitrack.logitrack.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private AuthService authService;

    private UserDTO userDTO;
    private Client client;
    private UserResponseDTO userResponseDTO;
    private UUID userId;
    private String email;
    private String password;
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        email = "test@example.com";
        password = "TestPassword123!";
        hashedPassword = "$2a$10$hashedPassword";

        userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setPasswordHash(password);
        userDTO.setRole(Role.CLIENT);
        userDTO.setName("Test User");

        client = Client.builder()
                .id(userId)
                .email(email)
                .passwordHash(hashedPassword)
                .name("Test User")
                .role(Role.CLIENT)
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id(userId.toString())
                .email(email)
                .name("Test User")
                .role(Role.CLIENT)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() {
        // Arrange
        String sessionId = "session-123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(client));
        when(httpSession.getId()).thenReturn(sessionId);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(userResponseDTO);

        try (MockedStatic<PaswordUtil> passwordUtil = mockStatic(PaswordUtil.class)) {
            passwordUtil.when(() -> PaswordUtil.verifyPassword(password, hashedPassword))
                    .thenReturn(true);

            // Act
            HttpSession result = authService.login(email, password, httpSession);

            // Assert
            assertNotNull(result);
            verify(userRepository, times(1)).findByEmail(email);
            verify(httpSession, times(1)).setAttribute(eq(sessionId), any(UserResponseDTO.class));
        }
    }

    @Test
    @DisplayName("Should throw exception when email not found")
    void testLoginEmailNotFound() {
        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.login(email, password, httpSession);
        });
        verify(userRepository, times(1)).findByEmail(email);
        verify(httpSession, never()).setAttribute(anyString(), any());
    }

    @Test
    @DisplayName("Should throw exception when password is invalid")
    void testLoginInvalidPassword() {
        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(client));

        try (MockedStatic<PaswordUtil> passwordUtil = mockStatic(PaswordUtil.class)) {
            passwordUtil.when(() -> PaswordUtil.verifyPassword(password, hashedPassword))
                    .thenReturn(false);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                authService.login(email, password, httpSession);
            });
            verify(userRepository, times(1)).findByEmail(email);
            verify(httpSession, never()).setAttribute(anyString(), any());
        }
    }

    @Test
    @DisplayName("Should register user with role CLIENT successfully")
    void testRegisterUserClientSuccess() {
        // Arrange
        userDTO.setRole(Role.CLIENT);
        Client clientEntity = Client.builder()
                .id(userId)
                .email(email)
                .passwordHash(hashedPassword)
                .name("Test User")
                .role(Role.CLIENT)
                .build();
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userMapper.toClientEntity(any(UserDTO.class))).thenReturn(clientEntity);
        when(userRepository.save(any(Client.class))).thenReturn(clientEntity);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(userResponseDTO);

        try (MockedStatic<PaswordUtil> passwordUtil = mockStatic(PaswordUtil.class)) {
            passwordUtil.when(() -> PaswordUtil.hashPassword(password))
                    .thenReturn(hashedPassword);

            // Act
            UserResponseDTO result = authService.registerUser(userDTO);

            // Assert
            assertNotNull(result);
            assertEquals(email, result.getEmail());
            verify(userRepository, times(1)).existsByEmail(email);
            verify(userMapper, times(1)).toClientEntity(any(UserDTO.class));
            verify(userRepository, times(1)).save(any(Client.class));
        }
    }

    @Test
    @DisplayName("Should register user with role ADMIN successfully")
    void testRegisterUserAdminSuccess() {
        // Arrange
        userDTO.setRole(Role.ADMIN);
        Admin adminEntity = Admin.builder()
                .id(userId)
                .email(email)
                .passwordHash(hashedPassword)
                .name("Test User")
                .role(Role.ADMIN)
                .build();
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userMapper.toAdminEntity(any(UserDTO.class))).thenReturn(adminEntity);
        when(userRepository.save(any(Admin.class))).thenReturn(adminEntity);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(userResponseDTO);

        try (MockedStatic<PaswordUtil> passwordUtil = mockStatic(PaswordUtil.class)) {
            passwordUtil.when(() -> PaswordUtil.hashPassword(password))
                    .thenReturn(hashedPassword);

            // Act
            UserResponseDTO result = authService.registerUser(userDTO);

            // Assert
            assertNotNull(result);
            assertEquals(email, result.getEmail());
            verify(userRepository, times(1)).existsByEmail(email);
            verify(userMapper, times(1)).toAdminEntity(any(UserDTO.class));
            verify(userRepository, times(1)).save(any(Admin.class));
        }
    }

    @Test
    @DisplayName("Should register user with role WAREHOUSE_MANAGER successfully")
    void testRegisterUserWarehouseManagerSuccess() {
        // Arrange
        userDTO.setRole(Role.WAREHOUSE_MANAGER);
        WAREHOUSE_MANAGER wmEntity = WAREHOUSE_MANAGER.builder()
                .id(userId)
                .email(email)
                .passwordHash(hashedPassword)
                .name("Test User")
                .role(Role.WAREHOUSE_MANAGER)
                .build();
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userMapper.toWarehouseManagerEntity(any(UserDTO.class))).thenReturn(wmEntity);
        when(userRepository.save(any(WAREHOUSE_MANAGER.class))).thenReturn(wmEntity);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(userResponseDTO);

        try (MockedStatic<PaswordUtil> passwordUtil = mockStatic(PaswordUtil.class)) {
            passwordUtil.when(() -> PaswordUtil.hashPassword(password))
                    .thenReturn(hashedPassword);

            // Act
            UserResponseDTO result = authService.registerUser(userDTO);

            // Assert
            assertNotNull(result);
            assertEquals(email, result.getEmail());
            verify(userRepository, times(1)).existsByEmail(email);
            verify(userMapper, times(1)).toWarehouseManagerEntity(any(UserDTO.class));
            verify(userRepository, times(1)).save(any(WAREHOUSE_MANAGER.class));
        }
    }

    @Test
    @DisplayName("Should throw exception when email already exists during registration")
    void testRegisterUserEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(userDTO);
        });
        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, never()).save(any());
    }
}
