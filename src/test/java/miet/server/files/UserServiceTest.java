package miet.server.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private UserService userService;

    private UserEntity testClient;

    @BeforeEach
    void setUp() {
        testClient = new UserEntity();
        testClient.setId(1L);
        testClient.setUsername("client_test");
        testClient.setPassword("encoded_password");
        testClient.setRole("CLIENT");
    }


    @Test
    void createClient_ShouldReturnClientWithRawPassword() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(testClient);
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");

        UserEntity result = userService.createClient();

        assertNotNull(result);
        assertEquals("client_test", result.getUsername());
        assertNotNull(result.getRawPassword());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(passwordEncoder, times(1)).encode(any());
    }


    @Test
    void resetClientPassword_ShouldUpdatePassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(passwordEncoder.encode(any())).thenReturn("new_encoded_password");
        when(userRepository.save(any(UserEntity.class))).thenReturn(testClient);

        UserEntity result = userService.resetClientPassword(1L);

        assertNotNull(result.getRawPassword());
        verify(userRepository, times(1)).save(testClient);
        verify(passwordEncoder, times(1)).encode(any());
    }

    @Test
    void resetClientPassword_ShouldThrowException_WhenNotClient() {
        testClient.setRole("USER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testClient));

        assertThrows(RuntimeException.class, () -> userService.resetClientPassword(1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetClientPassword_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.resetClientPassword(99L));
    }


    @Test
    void deleteClient_ShouldDeleteSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testClient));

        userService.deleteClient(1L);

        verify(userRepository, times(1)).delete(testClient);
    }

    @Test
    void deleteClient_ShouldThrowException_WhenNotClient() {
        testClient.setRole("ADMIN");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testClient));

        assertThrows(RuntimeException.class, () -> userService.deleteClient(1L));
        verify(userRepository, never()).delete(any());
    }


    @Test
    void createUserByClient_ShouldReturnUserWithBucket() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });
        when(passwordEncoder.encode(any())).thenReturn("encoded_user_password");

        UserEntity result = userService.createUserByClient("newuser");

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("USER", result.getRole());
        assertNotNull(result.getRawPassword());
        verify(minioService, times(1)).ensureUserBucket("user-2");
    }

    @Test
    void createUserByClient_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.findByUsername("existing_user")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(RuntimeException.class, () -> userService.createUserByClient("existing_user"));
        verify(userRepository, never()).save(any());
    }


    @Test
    void countClients_ShouldReturnCorrectCount() {
        when(userRepository.countByRole("CLIENT")).thenReturn(5L);

        long count = userService.countClients();

        assertEquals(5L, count);
    }

    @Test
    void countUsers_ShouldReturnCorrectCount() {
        when(userRepository.countByRole("USER")).thenReturn(10L);

        long count = userService.countUsers();

        assertEquals(10L, count);
    }
}