package miet.server.files;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final UserService userService;

    public ClientController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, String>> createUser(@RequestParam String username) {
        UserEntity user = userService.createUserByClient(username);
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "password", user.getRawPassword()
        ));
    }
}