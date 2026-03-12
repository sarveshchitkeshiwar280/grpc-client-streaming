// ============================================
// PROPER HealthController.java
// Replace your existing health endpoint with this
// ============================================

package com.yourpackage.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private OrderRepository orderRepository;  // If you have this
    
    @Autowired  
    private StockRepository stockRepository;  // If you have this
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", "Stock Trading Server");
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        try {
            // METHOD 1: Using JdbcTemplate (MOST RELIABLE)
            Long realOrderCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM stock_orders", 
                Long.class
            );
            
            Long realStockCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM stocks", 
                Long.class
            );
            
            // METHOD 2: Using Repository (if available)
            Long repoOrderCount = null;
            Long repoStockCount = null;
            
            try {
                if (orderRepository != null) {
                    repoOrderCount = orderRepository.count();
                }
                if (stockRepository != null) {
                    repoStockCount = stockRepository.count();
                }
            } catch (Exception e) {
                // Repository might not be available
            }
            
            Map<String, Object> database = new HashMap<>();
            database.put("connected", true);
            database.put("orders", realOrderCount);
            database.put("stocks", realStockCount);
            
            // Add repository counts for comparison
            if (repoOrderCount != null) {
                database.put("ordersFromRepository", repoOrderCount);
            }
            if (repoStockCount != null) {
                database.put("stocksFromRepository", repoStockCount);
            }
            
            // Add table info for debugging
            try {
                List<Map<String, Object>> orderTableInfo = jdbcTemplate.queryForList(
                    "DESCRIBE stock_orders"
                );
                database.put("orderTableColumns", orderTableInfo.size());
                
                // Verify counts with alternative query
                Long altCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(id) FROM stock_orders",
                    Long.class
                );
                database.put("alternativeOrderCount", altCount);
                
            } catch (Exception e) {
                database.put("tableCheckError", e.getMessage());
            }
            
            health.put("database", database);
            
        } catch (Exception e) {
            Map<String, Object> database = new HashMap<>();
            database.put("connected", false);
            database.put("error", e.getMessage());
            health.put("database", database);
            health.put("status", "DOWN");
        }
        
        return health;
    }
    
    // Additional endpoint to force refresh
    @GetMapping("/health/refresh")
    public String refreshHealth() {
        return "Health stats refreshed at: " + LocalDateTime.now();
    }
    
    // Debug endpoint to see raw database counts
    @GetMapping("/health/debug")
    public Map<String, Object> healthDebug() {
        Map<String, Object> debug = new HashMap<>();
        
        try {
            // Multiple ways to count orders
            debug.put("count_method1", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM stock_orders", Long.class));
            
            debug.put("count_method2", jdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM stock_orders", Long.class));
            
            debug.put("count_method3", jdbcTemplate.queryForObject(
                "SELECT COUNT(order_id) FROM stock_orders", Long.class));
            
            // List all order IDs for verification
            List<String> orderIds = jdbcTemplate.queryForList(
                "SELECT order_id FROM stock_orders ORDER BY created_at DESC",
                String.class
            );
            debug.put("allOrderIds", orderIds);
            debug.put("totalUniqueOrders", orderIds.size());
            
            // Check for duplicates
            List<String> duplicateIds = jdbcTemplate.queryForList(
                "SELECT order_id FROM stock_orders GROUP BY order_id HAVING COUNT(*) > 1",
                String.class
            );
            debug.put("duplicateOrderIds", duplicateIds);
            
        } catch (Exception e) {
            debug.put("error", e.getMessage());
        }
        
        return debug;
    }
}
