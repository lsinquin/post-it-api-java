package fr.lsinquin.postit.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring security filter responsible for collecting and validating JWTs.
 * If a JWT is successfully collected and validated, the appropriate User is set in the Security Context.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Filters incoming HTTP requests.
     * It looks for a JWT in the Authorization header of the incoming request. If the JWT is valid, it sets a complete authenticated user in the security context.
     * @param httpServletRequest {@inheritDoc}
     * @param httpServletResponse {@inheritDoc}
     * @param filterChain {@inheritDoc}
     * @throws ServletException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        // If no JWT is present, the Spring security chain carries on.
        if (!isJWTPresent(header)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        final String jwtToken = parseHeader(header);

        // If The JWT is not valid, the Spring security chain carries on.
        if (!jwtTokenUtil.validate(jwtToken)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        setSecurityContext(httpServletRequest, jwtToken);

        // The Spring security filter chain carries on with a authenticated User in his Security Context.
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * Checks if a JWT token is correctly put in a String.
     * The JWT token must be specified as a Bearer token.
     * @param header String representing a header field value.
     * @return True if a JWT token is present. False otherwise
     */
    private boolean isJWTPresent(String header) {
        return header != null && header.startsWith("Bearer ");
    }

    /**
     * Retrieves a JWT from a String representing a Bearer token.
     * Usually, {@link #isJWTPresent(String) isJWTPresent} is called before to verify the presence of a JWT in this String.
     * @param header String representing a header field value.
     * @return String representing a JWT
     */
    private String parseHeader(String header) {
        return header.split(" ")[1].trim();
    }

    /**
     * Retrieves the user from a JWT and put it in the security context.
     * @param request Current HTTP request
     * @param validatedJwtToken String representing a valid JWT
     */
    private void setSecurityContext(HttpServletRequest request, String validatedJwtToken) {
        // Retrieves the username from the JWT
        String username = jwtTokenUtil.getUserSubject(validatedJwtToken);

        // Retrieves the complete user thanks to his name
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Create a authenticated User Token containing the UserDetails instance
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        // Include the authenticated user in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
