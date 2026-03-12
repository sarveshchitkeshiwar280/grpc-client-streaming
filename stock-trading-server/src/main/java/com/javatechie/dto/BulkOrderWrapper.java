// BulkOrderWrapper.java (for gRPC)
package com.javatechie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkOrderWrapper {
    private List<StockOrderDto> orders;
    private String batchId;
    private String userId;
}