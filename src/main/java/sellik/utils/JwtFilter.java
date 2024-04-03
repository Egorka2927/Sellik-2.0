package sellik.utils;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = null;
        String username = null;
        UserDetails userDetails = null;
        UsernamePasswordAuthenticationToken authenticationToken = null;

        try {

            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                jwtToken = headerAuth.substring(7);
            }

            String refreshToken = extractRefreshTokenFromCookie(request);

            if (jwtToken != null && refreshToken != null) {
                try {
                    username = jwtUtil.getUsername(jwtToken);
                } catch (ExpiredJwtException e) {
                    try {
                        username = jwtUtil.getUsername(refreshToken);
                        jwtToken = jwtUtil.generateToken(username);
                        refreshToken = jwtUtil.generateRefreshToken(username);
                        ResponseCookie cookieRefreshJwt = createCookieWithRefreshJwt(refreshToken);
                        response.addHeader("Authorization", "Bearer " + jwtToken);
                        response.addHeader(HttpHeaders.SET_COOKIE, cookieRefreshJwt.toString());
                    } catch (ExpiredJwtException ex) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed: " + ex.getMessage());
                        return;
                    }
                }
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    userDetails = userDetailsService.loadUserByUsername(username);
                    authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    public ResponseCookie createCookieWithRefreshJwt(String refreshJwt) {
        ResponseCookie cookie = ResponseCookie.from("refreshJwt", refreshJwt)
                .httpOnly(true)
                .secure(true)
                .maxAge(60 * 2)
                .sameSite("Strict")
                .path("/")
                .build();
        return cookie;
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("refreshJwt".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
