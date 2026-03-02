package com.mini.cti.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter that runs on every HTTP request.
 * Extracts and validates JWT tokens from Authorization header.
 * If valid, sets user authentication in SecurityContext.
 *
 * @author S
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract JWT token from Authorization header
            String jwt = extractJwtFromRequest(request);

            // Validate token and authenticate user
            if (jwt != null &&
                    jwtService.validateToken(jwt)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Only authenticate if NOT already authenticated

                // Extract username from token
                String username = jwtService.extractUsername(jwt);

                // Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());

                // Set request details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }catch (ExpiredJwtException e){
            log.warn("Token has expired: {} ", e.getMessage());
        }catch (MalformedJwtException e){
            log.warn("Invalid token : {}", e.getMessage());
        }catch (Exception e){
            log.error("User could not be authenticated.", e);
        }

        // Continue filter chain
        filterChain.doFilter(request,response);

    }


    /**
     * Extracts JWT token from Authorization header of request
     * @param request   HTTP Request
     * @return          Token without (Bearer ) or null if it is not exist.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
