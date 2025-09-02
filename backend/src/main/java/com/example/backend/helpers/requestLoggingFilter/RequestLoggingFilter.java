package com.example.backend.helpers.requestLoggingFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("======= Incoming Request =======");
        System.out.println("Method: " + request.getMethod());
        System.out.println("Origin: " + request.getHeader("Origin"));
        System.out.println("Referer: " + request.getHeader("Referer"));
        System.out.println("Host: " + request.getHeader("Host"));
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Path: " + request.getServletPath());
        System.out.println("Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println(name + ": " + request.getHeader(name));
        }
        System.out.println("================================");

        filterChain.doFilter(request, response);
    }
}