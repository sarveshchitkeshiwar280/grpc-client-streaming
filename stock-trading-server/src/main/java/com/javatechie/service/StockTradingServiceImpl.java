package com.javatechie.service;

import com.javatechie.entity.OrderEntity;
import com.javatechie.entity.Stock;
import com.javatechie.grpc.*;
import com.javatechie.repository.OrderRepository;
import com.javatechie.repository.StockRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@GrpcService
@Service
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ---------- CLIENT STREAMING: bulkStockOrder ----------
    @Override
    public StreamObserver<StockOrder> bulkStockOrder(
            StreamObserver<OrderSummary> responseObserver) {

        System.out.println("[CLIENT STREAMING] Starting bulk order processing...");

        return new StreamObserver<StockOrder>() {
            private final AtomicInteger totalOrders = new AtomicInteger(0);
            private final AtomicInteger successCount = new AtomicInteger(0);
            private double totalAmount = 0.0;

            @Override
            public void onNext(StockOrder order) {
                totalOrders.incrementAndGet();
                System.out.println("📥 Processing order: " + order.getOrderId() +
                        " for " + order.getStockSymbol());

                if (isValidOrder(order)) {
                    successCount.incrementAndGet();
                    double orderTotal = order.getPrice() * order.getQuantity();
                    totalAmount += orderTotal;

                    try {
                        // Store in database with transaction
                        storeOrderInDatabase(order, orderTotal);
                        System.out.println(" Order " + order.getOrderId() + " saved to MySQL database");
                    } catch (Exception e) {
                        System.err.println(" Failed to save order to MySQL DB: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(" Order " + order.getOrderId() + " validation failed");
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error in bulk order stream: " + t.getMessage());
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                System.out.println("[CLIENT STREAMING] Completed processing " +
                        totalOrders.get() + " orders");

                OrderSummary summary = OrderSummary.newBuilder()
                        .setTotalOrders(totalOrders.get())
                        .setSuccessCount(successCount.get())
                        .setTotalAmount(totalAmount)
                        .build();

                responseObserver.onNext(summary);
                responseObserver.onCompleted();

                System.out.println("📊 Summary: " + totalOrders.get() + " orders, " +
                        successCount.get() + " successful, $" + String.format("%.2f", totalAmount) + " total");

                // Log database status
                logDatabaseStatus();
            }

            private boolean isValidOrder(StockOrder order) {
                return order != null &&
                        !order.getStockSymbol().isEmpty() &&
                        order.getQuantity() > 0 &&
                        order.getPrice() > 0 &&
                        (order.getOrderType().equals("BUY") || order.getOrderType().equals("SELL"));
            }

            @Transactional
            private void storeOrderInDatabase(StockOrder order, double orderTotal) {
                // Generate order ID if not provided
                String orderId = order.getOrderId();
                if (orderId == null || orderId.isEmpty()) {
                    orderId = "ORD-" + System.currentTimeMillis() + "-" + totalOrders.get();
                }

                // Check if order already exists to prevent duplicates
                OrderEntity existingOrder = orderRepository.findByOrderId(orderId);
                if (existingOrder != null) {
                    System.out.println(" Order " + orderId + " already exists in database, skipping...");
                    return;
                }

                // 1. Update or create Stock record
                Stock stock = stockRepository.findBySymbol(order.getStockSymbol())
                        .orElseGet(() -> {
                            Stock newStock = Stock.builder()
                                    .symbol(order.getStockSymbol())
                                    .name(order.getStockSymbol() + " Corporation")
                                    .price(order.getPrice())
                                    .build();
                            System.out.println(" Creating new stock entry for: " + order.getStockSymbol());
                            return stockRepository.save(newStock);
                        });

                // Update stock price if changed
                if (Math.abs(stock.getPrice() - order.getPrice()) > 0.01) {
                    stock.setPrice(order.getPrice());
                    stockRepository.save(stock);
                    System.out.println("Updated price for " + stock.getSymbol() + " to $" + order.getPrice());
                }

                // 2. Store individual order
                OrderEntity orderEntity = OrderEntity.builder()
                        .orderId(orderId)
                        .stockSymbol(order.getStockSymbol())
                        .quantity(order.getQuantity())
                        .price(order.getPrice())
                        .orderType(order.getOrderType())
                        .totalAmount(orderTotal)
                        .status("PROCESSED")
                        .stock(stock)
                        .build();

                orderRepository.save(orderEntity);
                System.out.println(" Saved order " + orderId + " to MySQL database");
            }

            private void logDatabaseStatus() {
                try {
                    long stockCount = stockRepository.count();
                    long orderCount = orderRepository.count();
                    System.out.println("🗄Database Status: " + stockCount + " stocks, " + orderCount + " orders");
                } catch (Exception e) {
                    System.err.println("Failed to get database status: " + e.getMessage());
                }
            }
        };
    }

    // ... [Keep other methods unchanged: getStockPrice, subscribeStockPrice, tradeStream]
}