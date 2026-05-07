package miet.server.files;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin/connections")
public class AdminConnectionController {

    private final UserService userService;

    public AdminConnectionController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserEntity> listConnections() {
        return userService.getAllClients();
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createConnection() {
        UserEntity client = userService.createClient();
        return ResponseEntity.ok(Map.of(
                "id", client.getId().toString(),
                "username", client.getUsername(),
                "password", client.getRawPassword()
        ));
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id) {
        UserEntity updated = userService.resetClientPassword(id);
        return ResponseEntity.ok(Map.of(
                "username", updated.getUsername(),
                "password", updated.getRawPassword()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConnection(@PathVariable Long id) {
        userService.deleteClient(id);
        return ResponseEntity.ok().build();
    }
}