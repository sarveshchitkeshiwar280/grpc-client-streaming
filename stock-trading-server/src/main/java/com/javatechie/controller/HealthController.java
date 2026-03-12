package com.javatechie.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Stock Trading Server");

        try {
            //  FIXED: Get REAL counts from database
            Long orderCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM stock_orders",
                    Long.class
            );

            Long stockCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM stocks",
                    Long.class
            );

            Map<String, Object> database = new HashMap<>();
            database.put("connected", true);
            database.put("orders", orderCount);    // REAL count
            database.put("stocks", stockCount);    // REAL count

            health.put("database", database);

        } catch (Exception e) {
            // If database query fails
            Map<String, Object> database = new HashMap<>();
            database.put("connected", false);
            database.put("error", e.getMessage());
            health.put("database", database);
            health.put("status", "DOWN");
        }

        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/actuator/health")
    public ResponseEntity<Map<String, Object>> actuatorHealth() {
        return health();
    }

    //  ADD THIS: Debug endpoint to verify counts
    @GetMapping("/api/real-count")
    public ResponseEntity<Map<String, Object>> getRealCounts() {
        Map<String, Object> counts = new HashMap<>();

        try {
            Long realOrders = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM stock_orders", Long.class);
            Long realStocks = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM stocks", Long.class);

            counts.put("realOrders", realOrders);
            counts.put("realStocks", realStocks);
            counts.put("timestamp", System.currentTimeMillis());
            counts.put("source", "Direct database query");
            counts.put("status", "SUCCESS");

        } catch (Exception e) {
            counts.put("error", e.getMessage());
            counts.put("status", "ERROR");
        }

        return ResponseEntity.ok(counts);
    }
}