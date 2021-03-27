package xyz.lcdev.winhacks2021;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestEndpoint {
    @GetMapping("/rest/getFacilities")
    public String getFacilities() {
        return "[{\"name\":\"Test site\",\"image\":\"/images/unknownSite.png\"},{\"name\":\"Test site 2\"}]";
    }
}
