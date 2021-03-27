package xyz.lcdev.winhacks2021.auth;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;


@ResponseBody
public class AuthError extends ResponseStatusException {
    private final int authErrorCode;
    private final String authErrorText;
    public AuthError(HttpStatus status,int authErrorCode,String text) {
        super(status);
        this.authErrorCode = authErrorCode;
        authErrorText = text;
    }
    
    public String getText(){
        return authErrorText;
    }
    
    public int getErrorCode(){
        return authErrorCode;
    }
}
