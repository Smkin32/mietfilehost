package miet.server.files;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MinioService minioService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MinioService minioService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.minioService = minioService;
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserEntity> getAllClients() {
        return userRepository.findByRole("CLIENT");
    }

    public long countClients() {
        return userRepository.countByRole("CLIENT");
    }

    @Transactional
    public UserEntity createClient() {
        String username = "client_" + UUID.randomUUID().toString().substring(0, 8);
        String rawPassword = generatePassword();
        UserEntity client = new UserEntity();
        client.setUsername(username);
        client.setPassword(passwordEncoder.encode(rawPassword));
        client.setRole("CLIENT");
        UserEntity saved = userRepository.save(client);
        saved.setRawPassword(rawPassword);
        return saved;
    }

    @Transactional
    public UserEntity resetClientPassword(Long id) {
        UserEntity client = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        if (!client.getRole().equals("CLIENT")) {
            throw new RuntimeException("Only client password can be reset");
        }
        String newRaw = generatePassword();
        client.setPassword(passwordEncoder.encode(newRaw));
        userRepository.save(client);
        client.setRawPassword(newRaw);
        return client;
    }

    @Transactional
    public void deleteClient(Long id) {
        UserEntity client = findById(id);
        if (!client.getRole().equals("CLIENT")) {
            throw new RuntimeException("Only clients can be deleted");
        }
        userRepository.delete(client);
    }

    public long countUsers() {
        return userRepository.countByRole("USER");
    }

    @Transactional
    public UserEntity createUserByClient(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        String rawPassword = generatePassword();
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");
        UserEntity saved = userRepository.save(user);
        String bucketName = "user-" + saved.getId();
        minioService.ensureUserBucket(bucketName);
        saved.setRawPassword(rawPassword);
        return saved;
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}