package com.livinggoodsbackend.livinggoodsbackend.security;

import com.livinggoodsbackend.livinggoodsbackend.security.CachedBodyHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // Public endpoints that skip JWT authentication
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/upload/profile-image", // Explicitly include this endpoint
            "/v3/api-docs",
            "/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars",
            "/error",
            "/favicon.ico",
            "/ws"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Log basic request info
        System.out.println("=== Incoming Request ===");
        System.out.println("Method: " + request.getMethod());
        System.out.println("URI: " + request.getRequestURI());

        // Log headers
        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> System.out.println(headerName + ": " + request.getHeader(headerName)));

        // Check if this is a multipart request and skip body caching
        String contentType = request.getContentType();
        HttpServletRequest effectiveRequest = request;
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/form-data") && 
            !"OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("Skipping body caching for multipart/form-data request");
        } else if ("POST".equalsIgnoreCase(request.getMethod()) || 
                   "PUT".equalsIgnoreCase(request.getMethod()) || 
                   "PATCH".equalsIgnoreCase(request.getMethod())) {
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
            String body = new String(wrappedRequest.getInputStream().readAllBytes());
            System.out.println("Body: " + body);
            effectiveRequest = wrappedRequest;
        }

        System.out.println("========================");

        String requestPath = effectiveRequest.getRequestURI();

        // Skip OPTIONS requests and public paths
        if ("OPTIONS".equalsIgnoreCase(effectiveRequest.getMethod()) || isPublicPath(requestPath)) {
            filterChain.doFilter(effectiveRequest, response);
            return;
        }

        String authHeader = effectiveRequest.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                System.err.println("Error extracting username from JWT: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(effectiveRequest)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                System.err.println("Error loading user details: " + e.getMessage());
            }
        }

        filterChain.doFilter(effectiveRequest, response);
    }

    private boolean isPublicPath(String requestPath) {
        return PUBLIC_PATHS.stream().anyMatch(requestPath::startsWith);
    }
}