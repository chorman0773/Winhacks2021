package xyz.lcdev.winhacks2021;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.lcdev.winhacks2021.auth.User;

import java.security.InvalidKeyException;
import java.sql.SQLException;

@RestController
public class RestEndpoint {
    @GetMapping("/rest/getFacilities")
    public String getFacilities() {
        return "[{\"name\":\"Test site\",\"image\":\"/images/unknownSite.png\"},{\"name\":\"Test site 2\"}]";
    }

    @GetMapping("/rest/getFacilityData")
    public String getFacilityData(@RequestParam String name) {
        return "{\"name\":\"Test site\",\"image\":\"SOMEONE GET ME A PLACEHOLDER\",\"location\":\"Smallville\"," +
                "\"equipment\":[" +
                "{\"name\":\"Centrifuge\",\"trained\":20,\"researchersUsing\":1,\"publications\":50,\"students\":2,\"samplesProcessed\":30}," +
                "{\"name\":\"Fluorometer\",\"trained\":2,\"researchersUsing\":3,\"publications\":0,\"students\":0,\"samplesProcessed\":100}]}";
    }

    @PostMapping(value="/auth/create-account",produces="application/json")
    public User createAccount(@RequestParam("uname") String userName, @RequestParam("email") String addr, @RequestParam("passwd") byte[] passwd) throws SQLException, InvalidKeyException {
        return User.createAccount(userName,addr,passwd);
    }

    @PostMapping(value="/auth/lookup")
    public User lookupAccount(@RequestParam("email") String addr) throws SQLException {
        return User.getByAddress(addr);
    }
}
