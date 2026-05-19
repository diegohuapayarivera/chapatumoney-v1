package com.dhr.chapatumoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "El backend está funcionando correctamente");

        try {
            // Hacemos una consulta muy ligera para probar la conexión a Supabase
            jdbcTemplate.execute("SELECT 1");
            response.put("database", "CONNECTED");
            response.put("database_provider", "Supabase PostgreSQL");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("database", "DISCONNECTED");
            response.put("error", e.getMessage());
            return ResponseEntity.status(503).body(response);
        }
    }
}
