package com.mini.cti.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
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
            final String jwt = extractJwtFromRequest(request);

            // Validate token and authenticate user
            if (jwt == null || SecurityContextHolder.getContext().getAuthentication() != null){
                filterChain.doFilter(request,response);
                return;
            }

                // Extract username from token
                String username = jwtService.extractSubject(jwt);

                // Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Token authentication
                if (jwtService.isTokenValid(jwt,userDetails)){

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());

                    // Set request details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }catch (ExpiredJwtException e){
            // triggers AuthenticationEntryPoint 401
            throw new AuthenticationCredentialsNotFoundException("Token has expired.");
        }catch (JwtException | IllegalArgumentException  e){
            throw new BadCredentialsException("Invalid token");
        }catch (BadCredentialsException e){
            // just leave to net filter
            throw e;
        }catch (Exception e){
            log.error("Unexpected error during validation", e);
            throw new AuthenticationCredentialsNotFoundException("Token validation failed.");
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
        final String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
