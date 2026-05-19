package com.dhr.chapatumoney.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter ya no es necesario — la validación JWT ahora la hace
 * Spring Security OAuth2 Resource Server vía JWKS (ver SecurityConfig).
 * Esta clase se mantiene vacía para no romper referencias existentes.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // OAuth2 Resource Server maneja la validación JWT automáticamente
        chain.doFilter(request, response);
    }
}
