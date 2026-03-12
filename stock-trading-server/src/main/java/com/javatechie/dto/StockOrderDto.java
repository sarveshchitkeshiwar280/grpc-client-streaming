package com.javatechie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOrderDto {
    private String orderId;
    private String stockSymbol;
    private String orderType; // "BUY" or "SELL"
    private double price;
    private int quantity;
    private String userId;
    private String portfolioId;
    private String timestamp;
}