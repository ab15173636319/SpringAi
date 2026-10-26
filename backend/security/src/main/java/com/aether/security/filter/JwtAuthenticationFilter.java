package com.aether.security.filter;

import com.aether.common.error.ApiException;
import com.aether.security.mapper.UserMapper;
import com.aether.security.pojo.entity.AuthenticatedUser;
import com.aether.security.pojo.entity.UserAccount;
import com.aether.security.service.JwtService;
import com.aether.security.service.TokenSessionService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TokenSessionService tokenSessionService;
    private final UserMapper userRepository;


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
                String sessionId = claims.get("sid", String.class);
                String tokenId = claims.getId();
                if (!StringUtils.hasText(sessionId)
                        || !StringUtils.hasText(tokenId)
                        || tokenSessionService.isAccessTokenBlacklisted(tokenId)
                        || !tokenSessionService.isActive(claims.getSubject(), sessionId)) {
                    throw ApiException.unauthorized("登录状态已失效，请重新登录");
                }

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


