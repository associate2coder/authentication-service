package ua.com.associate2coder.authenticationservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Value(value = "${html-request-auth-header}")
    private String AUTH_HEADER_KEY;
    @Value(value = "${jwt-prefix}")
    private  String TOKEN_PREFIX;



    @Override
    protected void doFilterInternal
            (
                    @NotNull HttpServletRequest request,
                    @NotNull HttpServletResponse response,
                    @NotNull FilterChain filterChain
            ) throws IOException, ServletException {

        final String authHeader = request.getHeader(AUTH_HEADER_KEY);
        String username = null;
        String authToken = null;

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            authToken = authHeader.substring(TOKEN_PREFIX.length());
            try {
                username = jwtService.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.error("An error has occurred during getting email from token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("The token has expired and no longer valid", e);
            } catch (SignatureException e) {
                logger.error("Authentication failed. Either email or password is not valid.");
            }
        } else {
            logger.warn("Bearer string was not found, the header will be ignored");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
                        (
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}

