// Create this class in dto package
package com.javatechie.dto;

import com.javatechie.entity.Stock;
import lombok.Data;

@Data
public class StockResponseDTO {
    private Long id;
    private String symbol;
    private String name;
    private double price;
    private String createdAt;
    private String updatedAt;
}



