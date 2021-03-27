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

    @PostMapping(value="/auth/create-account",produces="application/json")
    public User createAccount(@RequestParam("uname") String userName, @RequestParam("email") String addr, @RequestParam("passwd") byte[] passwd) throws SQLException, InvalidKeyException{
        return User.createAccount(userName,addr,passwd);
    }
}
