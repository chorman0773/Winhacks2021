package xyz.lcdev.winhacks2021;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.lcdev.winhacks2021.auth.User;

import java.security.InvalidKeyException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class RestEndpoint {
    @GetMapping(value="/rest/getFacilities",produces="application/json")
    public List<Facility> getFacilities() throws SQLException {
        Connection conn = Database.open();
        var stmt = conn.prepareStatement("SELECT * FROM Facilities");
        List<Facility> ret = new ArrayList<>();
        try(var rs = stmt.executeQuery()){
            while(rs.next()){
                var id = UUID.fromString(rs.getString(1));
                var name = rs.getString(2);
                var location = rs.getString(3);
                ret.add(new Facility(id,name,location));
            }
        }
        return ret;
    }

    @GetMapping("/rest/getFacilityData")
    public List<FacilityRow> getFacilityData(@RequestParam String id) throws SQLException {
        Connection conn = Database.open();
        id+=".Equipment";
        var stmt = conn.prepareStatement("SELECT * FROM ?");
        stmt.setString(1,id);
        List<FacilityRow> ret = new ArrayList<>();
        try(var rs = stmt.executeQuery()){
            while(rs.next()){
                ret.add(new FacilityRow(rs.getString(1),rs.getLong(2),rs.getLong(3),rs.getLong(4),rs.getLong(5),rs.getLong(6)));
            }
        }
        return ret;
    }

    @PostMapping(value="/auth/create-account",produces="application/json")
    public User createAccount(@RequestParam("uname") String userName, @RequestParam("email") String addr, @RequestParam("passwd") byte[] passwd) throws SQLException, InvalidKeyException {
        return User.createAccount(userName,addr,passwd);
    }

    @PostMapping(value="/auth/lookup")
    public User lookupAccount(@RequestParam("email") String addr) throws SQLException {
        return User.getByAddress(addr);
    }

    static class Facility {
        private final UUID id;
        private final String name;
        private final String location;

        Facility(UUID id, String name, String location) {
            this.id = id;
            this.name = name;
            this.location = location;
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLocation() {
            return location;
        }
    }

    static class FacilityRow{
        private final String name;
        private final long trained;
        private final long researchers;
        private final long publications;
        private final long students;
        private final long samples;

        FacilityRow(String name, long trained, long researchers, long publications, long students, long samples) {
            this.name = name;
            this.trained = trained;
            this.researchers = researchers;
            this.publications = publications;
            this.students = students;
            this.samples = samples;
        }

        public String getName() {
            return name;
        }

        public long getTrained() {
            return trained;
        }

        public long getResearchers() {
            return researchers;
        }

        public long getPublications() {
            return publications;
        }

        public long getStudents() {
            return students;
        }

        public long getSamples() {
            return samples;
        }
    }
}
