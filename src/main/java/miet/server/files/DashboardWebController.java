package miet.server.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardWebController {

    private final UserService userService;
    private final ApiRequestCounter apiRequestCounter;

    @Value("${minio.console-url}")
    private String minioConsoleUrl;

    public DashboardWebController(UserService userService, ApiRequestCounter apiRequestCounter) {
        this.userService = userService;
        this.apiRequestCounter = apiRequestCounter;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalClients", userService.countClients());
        model.addAttribute("totalRequests", apiRequestCounter.getTotalRequests());
        model.addAttribute("clients", userService.getAllClients());
        model.addAttribute("minioConsoleUrl", minioConsoleUrl);
        return "dashboard";
    }
}