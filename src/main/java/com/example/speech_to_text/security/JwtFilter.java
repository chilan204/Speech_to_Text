package com.example.speech_to_text.security;

import com.example.speech_to_text.enums.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                try {
                    username = jwtUtil.extractUsername(token);
                } catch (SignatureException e) {
                    logger.warn("Token is invalid!");
                } catch (ExpiredJwtException e) {
                    logger.warn("Token has been expired!");
                }
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    String roleStr = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
                    if (roleStr != null) {
                        try {
                            UserRole role = UserRole.valueOf(roleStr);
                            var authorities = Collections.singletonList(new SimpleGrantedAuthority(role.name()));
                            var authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, authorities
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } catch (IllegalArgumentException e) {
                            logger.error("Invalid role in token: " + roleStr);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("JWT Filter error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}