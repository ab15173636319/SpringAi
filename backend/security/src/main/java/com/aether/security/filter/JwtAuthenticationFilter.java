package com.aether.security.filter;

import com.aether.security.mapper.UserMapper;
import com.aether.security.pojo.entity.AuthenticatedUser;
import com.aether.security.pojo.entity.UserAccount;
import com.aether.security.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserMapper userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserMapper userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.parse(header.substring(7), JwtService.ACCESS);
                UserAccount account = userRepository.findById(claims.getSubject())
                        .filter(UserAccount::isEnabled)
                        .orElse(null);
                if (account != null) {
                    AuthenticatedUser principal = new AuthenticatedUser(
                            account.getId(),
                            account.getUsername(),
                            account.getDisplayName(),
                            account.getAvatarColor(),
                            account.getRole()
                    );
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    principal.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (RuntimeException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}



