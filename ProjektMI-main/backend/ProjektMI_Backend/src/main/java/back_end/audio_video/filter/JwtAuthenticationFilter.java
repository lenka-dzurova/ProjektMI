package back_end.audio_video.filter;

import back_end.audio_video.component.JwtUtil;
import back_end.audio_video.details.PouzivatelDetails;
import back_end.audio_video.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String email = null;
        String jwt = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JWT")) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }


        if (jwt != null) {
            try {
                email = jwtUtil.extractEmail(jwt);
            } catch (ExpiredJwtException e) {
                logger.warn("JWT token bol expirovany");
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            PouzivatelDetails pouzivatelDetails = customUserDetailsService.loadUserByUsername(email);

            if (pouzivatelDetails != null && jwtUtil.validateToken(jwt, pouzivatelDetails.getEmail())) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(pouzivatelDetails, null, pouzivatelDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
