package xyz.lcdev.winhacks2021.auth;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@ResponseBody
public class AuthResponse {
    private final byte[] token;
    private final UUID accountID;
    private final Set<String> scopes;

    public AuthResponse(byte[] token, UUID accountID, Set<String> scopes) {
        this.token = token;
        this.accountID = accountID;
        this.scopes = scopes;
    }

    public String getToken(){
        return Base64.encodeBase64String(token);
    }

    public UUID getAccountID(){
        return accountID;
    }

    public Set<String> getScopes(){
        return Collections.unmodifiableSet(scopes);
    }

}
