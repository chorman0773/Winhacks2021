package xyz.lcdev.winhacks2021;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleEndpoint {
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
