package com.example.backend.repositories.customCsrfTokenRepository;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

@Component
public class CustomCsrfTokenRepository implements CsrfTokenRepository{

    private static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
    private static final String CSRF_PARAM_NAME = "_csrf";
    private static final long TOKEN_EXPIRATION_TIME = 10 * 60 * 1000;
    private final ConcurrentHashMap<String,Long> tokenTimestamps = new ConcurrentHashMap<>();

    @Override
    public CsrfToken generateToken(HttpServletRequest request){
        String token = UUID.randomUUID().toString();
        tokenTimestamps.put(token,System.currentTimeMillis());

        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAM_NAME, token);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token != null) {
            var cookie = new jakarta.servlet.http.Cookie("XSRF-TOKEN", token.getToken());
            cookie.setPath("/");
            cookie.setHttpOnly(false);
            cookie.setSecure(true); 
            response.addCookie(cookie);
        } else {
            var cookie = new jakarta.servlet.http.Cookie("XSRF-TOKEN", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        String token = request.getHeader(CSRF_HEADER_NAME);
        if (token != null && tokenTimestamps.containsKey(token)) {
            long createdTime = tokenTimestamps.get(token);
            if ((System.currentTimeMillis() - createdTime) < TOKEN_EXPIRATION_TIME) {
                return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAM_NAME, token);
            }
        }
        return null; 
    }

}
