package com.dhr.chapatumoney.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Ignoramos peticiones a rutas muy ruidosas como archivos estáticos o preflights (OPTIONS)
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        logger.info(">>> [INICIO] {} {}", method, uri);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            
            if (status >= 400 && status < 500) {
                logger.warn("<<< [FIN] {} -> {} {} (tomó {}ms)", status, method, uri, duration);
            } else if (status >= 500) {
                logger.error("<<< [FIN ERROR] {} -> {} {} (tomó {}ms)", status, method, uri, duration);
            } else {
                logger.info("<<< [FIN] {} -> {} {} (tomó {}ms)", status, method, uri, duration);
            }
        }
    }
}
