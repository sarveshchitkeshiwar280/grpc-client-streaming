package com.javatechie.controller;

import com.javatechie.dto.*;
import com.javatechie.entity.Stock;
import com.javatechie.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    //fully updated:"
    private final StockService stockService;

    @GetMapping("/test")
    public String stockTest() {
        return "StockController is working! Time: " + new java.util.Date();
    }

    // GET all stocks - FIXED return type
    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> getAllStocks() {
        List<StockResponseDTO> stocks = stockService.getAllStocks().stream()
                .map(this::convertToResponse)
                .toList();
        return ResponseEntity.ok(stocks);
    }

    // GET stock by symbol - FIXED return type
    @GetMapping("/{symbol}")
    public ResponseEntity<StockResponseDTO> getStockBySymbol(@PathVariable String symbol) {
        Stock stock = stockService.getStockBySymbol(symbol);
        return ResponseEntity.ok(convertToResponse(stock));
    }

    // POST create new stock - FIXED return type
    @PostMapping
    public ResponseEntity<StockResponseDTO> createStock(@RequestBody StockRequest request) {
        Stock stock = Stock.builder()
                .symbol(request.getSymbol())
                .name(request.getName())
                .price(request.getPrice())
                .build();

        Stock savedStock = stockService.createStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToResponse(savedStock));
    }

    // PUT update stock price - FIXED return type
    @PutMapping("/{symbol}/price")
    public ResponseEntity<StockResponseDTO> updateStockPrice(
            @PathVariable String symbol,
            @RequestBody UpdatePriceRequest request) {

        Stock updatedStock = stockService.updateStockPrice(symbol, request.getPrice());
        return ResponseEntity.ok(convertToResponse(updatedStock));
    }

    // DELETE stock
    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> deleteStock(@PathVariable String symbol) {
        stockService.deleteStock(symbol);
        return ResponseEntity.noContent().build();
    }

    // ==================== BULK ORDER ENDPOINT ====================
    @PostMapping("/bulk-order")
    public ResponseEntity<Map<String, Object>> processBulkOrders(@RequestBody List<BulkOrderRequest> orders) {
        System.out.println(" Processing bulk orders: " + orders.size() + " orders");

        List<Map<String, Object>> results = new ArrayList<>();
        double totalAmount = 0;
        int successCount = 0;

        for (BulkOrderRequest order : orders) {
            Map<String, Object> result = new HashMap<>();

            try {
                // Validate stock exists
                Stock stock = stockService.getStockBySymbol(order.getStockSymbol());

                // Process order
                double amount = order.getPrice() * order.getQuantity();

                result.put("orderId", order.getOrderId());
                result.put("symbol", order.getStockSymbol());
                result.put("orderType", order.getOrderType());
                result.put("quantity", order.getQuantity());
                result.put("price", order.getPrice());
                result.put("amount", amount);
                result.put("status", "SUCCESS");
                result.put("message", order.getOrderType() + " order processed successfully");

                totalAmount += amount;
                successCount++;

            } catch (Exception e) {
                result.put("orderId", order.getOrderId());
                result.put("symbol", order.getStockSymbol());
                result.put("status", "FAILED");
                result.put("message", "Error: " + e.getMessage());
                result.put("amount", 0);
            }

            results.add(result);
        }

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("timestamp", new Date().toString());
        response.put("totalOrders", orders.size());
        response.put("successCount", successCount);
        response.put("failedCount", orders.size() - successCount);
        response.put("totalAmount", totalAmount);
        response.put("orders", results);

        System.out.println(" Bulk order processing complete: " + successCount + " successful");

        return ResponseEntity.ok(response);
    }
    // ==================== END BULK ORDER ====================

    // Convert Entity to DTO
    private StockResponseDTO convertToResponse(Stock stock) {
        StockResponseDTO dto = new StockResponseDTO();
        dto.setId(stock.getId());
        dto.setSymbol(stock.getSymbol());
        dto.setName(stock.getName());
        dto.setPrice(stock.getPrice());
        dto.setCreatedAt(stock.getCreatedAt().toString());
        dto.setUpdatedAt(stock.getUpdatedAt().toString());
        return dto;
    }
}