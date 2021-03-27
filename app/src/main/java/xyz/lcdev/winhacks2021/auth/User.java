package xyz.lcdev.winhacks2021.auth;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.PostMapping;
import xyz.lcdev.winhacks2021.Database;

import javax.crypto.Mac;
import javax.sql.RowSet;
import java.nio.file.attribute.UserPrincipal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class User implements UserPrincipal {
    private UUID userid;
    private String addrHash;
    private String uname;
    private byte[] authKey;
    private byte[] salt;
    private long permissions;

    private boolean dirty;

    private static final MessageDigest SHA256;
    private static final Mac HMAC_SHA512;

    static{
        try {
            SHA256 = MessageDigest.getInstance("SHA-256");
            HMAC_SHA512 = Mac.getInstance("HmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e); // panic!
        }
    }

    private static final Map<String,User> unameCache = new ConcurrentHashMap<>();

    public User(UUID id, String addr, byte[] auth, byte[] authSalt, String uname, long permissions) {
        this.userid = id;
        this.addrHash = addr;
        this.authKey = auth;
        this.salt = authSalt;
        this.uname = uname;
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userid.equals(user.userid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid);
    }

    @PostMapping(path="/auth/create-account",produces="application/json")
    public static void createAccount(String name,String addr,byte[] passwd){

    }

    @PostMapping("/auth/lookup_name")
    public static Optional<User> findByName(String uname) throws SQLException{
        if(unameCache.containsKey(uname))
            return Optional.of(unameCache.get(uname));
        else{
            Connection conn = Database.open();
            if(!conn.getMetaData().getTables(null,null,"Users",null).first()){
                return Optional.empty();
            }
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE UserName=?");
            stmt.setString(1,uname);
            try(var v = stmt.executeQuery()){
                if(!v.next())
                    return Optional.empty();
                var id = UUID.fromString(v.getString(1));
                var addr =  v.getString(2);
                var auth = Base64.decodeBase64(v.getString(3));
                var authSalt = Base64.decodeBase64(v.getString(4));
                var permissions = v.getLong(6);
                User ncur = new User(id,addr,auth,authSalt,uname,permissions);

                synchronized(User.class){
                    var u = unameCache.putIfAbsent(uname,ncur);
                    return Optional.of(Objects.requireNonNullElse(u, ncur));
                }
            }
        }
    }

    public static Optional<User> newUser(byte[] addrHash, byte[] authsrc, String uname) throws SQLException {
        Connection conn = Database.open();
        if(!conn.getMetaData().getTables(null,null,"Users",null).first()){
            conn.createStatement().executeUpdate("""
                CREATE TABLE "Users"(
                    UUID TEXT NONNULL,
                    AddrHash TEXT NONNULL,
                    AuthSrc TEXT,
                    AuthSalt TEXT,
                    UserName TEXT NONNULL,
                    PERMISSIONS INTEGER
                )
                """);
        }

        String addrb64 = Base64.encodeBase64String(addrHash);
        var find = conn.prepareStatement("SELECT * FROM Users WHERE AddrHash=?");
        find.setString(1,addrb64);
        try(var rs = find.executeQuery()){
            if(rs.next())
                return Optional.empty();
        }
        var add = conn.prepareStatement("INSERT INTO Users VALUES (?,?,?,?,?,?)");


        return Optional.empty();
    }

    @Override
    public String getName() {
        return userid.toString();
    }
}
