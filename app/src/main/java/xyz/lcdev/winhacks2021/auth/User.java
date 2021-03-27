package xyz.lcdev.winhacks2021.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.lcdev.winhacks2021.Database;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.RowSet;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.UserPrincipal;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class User implements UserPrincipal {
    private final UUID userid;
    private final String addrHash;
    private final String uname;
    private final byte[] authKey;
    private final byte[] salt;
    private final long permissions;

    private boolean dirty;

    private static final MessageDigest SHA256;
    private static final Mac HMAC_SHA512;
    private static final SecureRandom RANDOM;

    static{
        try {
            SHA256 = MessageDigest.getInstance("SHA-256");
            HMAC_SHA512 = Mac.getInstance("HmacSHA512");
            RANDOM = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e); // panic!
        }
    }
    private static final Object cacheInsertLock = new Object();
    private static final Map<UUID,User> uuidCache = new ConcurrentHashMap<>();
    private static final Map<byte[],User> addrCache = new ConcurrentHashMap<>();

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


    public static User createAccount( String userName, String addr, byte[] passwd) throws SQLException, InvalidKeyException {
        Connection conn = Database.open();
        if(!conn.getMetaData().getTables(null,null,"Users",null).next()){
            conn.createStatement().executeUpdate(
                "CREATE TABLE \"Users\"(\n" +
                    "UUID TEXT NONNULL,    \n" +
                    "AddrHash TEXT NONNULL,\n" +
                    "AuthSrc TEXT,         \n" +
                    "AuthSalt TEXT,        \n" +
                    "UserName TEXT NONNULL,\n" +
                    "PERMISSIONS INTEGER   \n" +
                ");"
                );
        }
        byte[] addrHash = SHA256.digest(addr.getBytes(StandardCharsets.UTF_8));
        String addrHashb64 = Base64.encodeBase64String(addrHash);
        var find = conn.prepareStatement("SELECT UserName FROM Users WHERE AddrHash=?");
        find.setString(1,addrHashb64);
        try(var rs = find.executeQuery()){
            if(rs.next())
                throw new AuthError(HttpStatus.FORBIDDEN,128,"Email Address Taken");
        }

        byte[] salt = new byte[64];
        RANDOM.nextBytes(salt);
        for(int i = 0;i<512;i++) {
            SecretKey key = new SecretKeySpec(salt, "HmacSHA512");
            HMAC_SHA512.init(key);
            passwd = HMAC_SHA512.doFinal(passwd);
        }
        String passwdb64 = Base64.encodeBase64String(passwd);
        String saltb64 = Base64.encodeBase64String(salt);
        UUID id = UuidUtil.getTimeBasedUuid();


        User ret = new User(id,addrHashb64,passwd,salt,userName,3);
        synchronized (cacheInsertLock){
            if(addrCache.putIfAbsent(addrHash,ret)!=null)
                throw new AuthError(HttpStatus.FORBIDDEN,128,"Email Address Taken");
            uuidCache.put(id,ret);
        }

        System.out.printf("/auth/create-user: Created %s (%s, %s)",userName,addrHashb64,id.toString());

        var create = conn.prepareStatement("INSERT INTO Users VALUES (?,?,?,?,?,?)");
        create.setString(1,id.toString());
        create.setString(2,addrHashb64);
        create.setString(3,passwdb64);
        create.setString(4,saltb64);
        create.setString(5,userName);
        create.setLong(6,3);
        create.executeUpdate();
        return ret;
    }


    @Override
    @JsonIgnore
    public String getName() {
        return userid.toString();
    }

    public UUID getUserId(){
        return userid;
    }

    public String getDisplayName(){
        return uname;
    }
}
