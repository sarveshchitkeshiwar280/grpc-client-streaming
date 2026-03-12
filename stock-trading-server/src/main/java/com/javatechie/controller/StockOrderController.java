package com.javatechie.controller;

import com.javatechie.dto.*;
import com.javatechie.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/orders")
public class StockOrderController {

    @GetMapping("/test")
    public String test() {
        return "StockOrderController WORKING! Time: " + new Date();
    }

    // ==================== FOR UI (REST API) ====================
    @PostMapping("/bulk")
    public Map<String, Object> processBulkOrdersUI(@RequestBody List<BulkOrderRequest> orders) {
        // Convert UI format to gRPC format
        BulkOrderWrapper wrapper = new BulkOrderWrapper();
        List<StockOrderDto> stockOrders = new ArrayList<>();

        for (BulkOrderRequest uiOrder : orders) {
            StockOrderDto dto = new StockOrderDto();
            dto.setOrderId(uiOrder.getOrderId());
            dto.setStockSymbol(uiOrder.getStockSymbol());
            dto.setOrderType(uiOrder.getOrderType());
            dto.setPrice(uiOrder.getPrice());
            dto.setQuantity(uiOrder.getQuantity());
            dto.setUserId(uiOrder.getUserId());
            dto.setPortfolioId(uiOrder.getPortfolioId());
            dto.setTimestamp(uiOrder.getTimestamp());
            stockOrders.add(dto);
        }

        wrapper.setOrders(stockOrders);
        wrapper.setBatchId("BATCH-" + System.currentTimeMillis());
        wrapper.setUserId("UI-USER");

        return processBulkOrdersGRPC(wrapper);
    }

    // ==================== FOR gRPC ====================
    @PostMapping("/bulk-stream")
    public Map<String, Object> processBulkOrdersGRPC(@RequestBody BulkOrderWrapper request) {
        Map<String, Object> response = new HashMap<>();

        try {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                    .usePlaintext()
                    .build();

            StockTradingServiceGrpc.StockTradingServiceStub asyncStub =
                    StockTradingServiceGrpc.newStub(channel);

            final OrderSummary[] summaryHolder = new OrderSummary[1];
            final CountDownLatch finishLatch = new CountDownLatch(1);

            StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
                @Override
                public void onNext(OrderSummary summary) {
                    summaryHolder[0] = summary;
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("gRPC Error: " + t.getMessage());
                    response.put("error", t.getMessage());
                    finishLatch.countDown();
                }

                @Override
                public void onCompleted() {
                    finishLatch.countDown();
                }
            };

            StreamObserver<StockOrder> requestObserver = asyncStub.bulkStockOrder(responseObserver);

            List<StockOrderDto> orders = request.getOrders();
            for (int i = 0; i < orders.size(); i++) {
                StockOrderDto dto = orders.get(i);

                StockOrder grpcOrder = StockOrder.newBuilder()
                        .setOrderId(dto.getOrderId() != null ? dto.getOrderId() :
                                "ORD-" + (i + 1) + "-" + System.currentTimeMillis())
                        .setStockSymbol(dto.getStockSymbol())
                        .setQuantity(dto.getQuantity())
                        .setPrice(dto.getPrice())
                        .setOrderType(dto.getOrderType())
                        .build();

                System.out.println("📤 Sending order: " + grpcOrder.getOrderId() +
                        " - " + grpcOrder.getStockSymbol());

                requestObserver.onNext(grpcOrder);
                Thread.sleep(500);
            }

            requestObserver.onCompleted();
            boolean completed = finishLatch.await(30, TimeUnit.SECONDS);

            if (!completed) {
                response.put("status", "TIMEOUT");
                response.put("message", "Server response timeout");
            } else if (summaryHolder[0] != null) {
                response.put("status", "SUCCESS");
                response.put("summaryId", "SUM-" + System.currentTimeMillis());
                response.put("totalOrders", summaryHolder[0].getTotalOrders());
                response.put("totalAmount", summaryHolder[0].getTotalAmount());
                response.put("successCount", summaryHolder[0].getSuccessCount());
                response.put("timestamp", new Date().toString());
            } else {
                response.put("status", "SUCCESS");
                response.put("message", "Orders processed (no summary returned)");
            }

            channel.shutdown();

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    // ==================== TEST ENDPOINT ====================
    @PostMapping("/test")
    public Map<String, Object> testBulkOrder() {
        List<BulkOrderRequest> testOrders = Arrays.asList(
                createUIOrder("AAPL", 100, 175.50, "BUY"),
                createUIOrder("GOOGL", 50, 142.25, "BUY"),
                createUIOrder("TSLA", 25, 210.75, "SELL")
        );

        return processBulkOrdersUI(testOrders);
    }

    private BulkOrderRequest createUIOrder(String symbol, int quantity, double price, String type) {
        BulkOrderRequest order = new BulkOrderRequest();
        order.setOrderId("TEST-" + System.currentTimeMillis() + "-" + symbol);
        order.setStockSymbol(symbol);
        order.setQuantity(quantity);
        order.setPrice(price);
        order.setOrderType(type);
        order.setUserId("test-user");
        order.setPortfolioId("test-portfolio");
        order.setTimestamp(new Date().toString());
        return order;
    }
}