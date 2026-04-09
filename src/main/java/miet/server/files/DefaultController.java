package miet.server.files;


import org.springframework.web.bind.annotation.*;


@RestController
public class DefaultController {

    @GetMapping("/hello")
    public String sayHi(){
        return "Hello";
    }
}