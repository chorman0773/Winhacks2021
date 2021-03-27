package xyz.lcdev.winhacks2021;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleEndpoint {
    @GetMapping("/")
    public String index(Model model) {
        System.out.println("Meep");
        return "index";
    }

    @GetMapping("/error")
    public String error(Model model) {
        return "error";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        return "signup";
    }
}
