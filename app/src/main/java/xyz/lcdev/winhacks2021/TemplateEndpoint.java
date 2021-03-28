package xyz.lcdev.winhacks2021;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.lcdev.winhacks2021.auth.User;

import java.security.InvalidKeyException;
import java.sql.SQLException;

@Controller
public class TemplateEndpoint {
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/db")
    public String db(Model model) {
        return "db";
    }

    @GetMapping("/error")
    public String error(Model model) {
        return "error";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        return "signup";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
}
