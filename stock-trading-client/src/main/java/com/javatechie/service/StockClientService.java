package com.javatechie.service;

import com.javatechie.grpc.StockOrder;
import com.javatechie.grpc.StockRequest;
import com.javatechie.grpc.StockResponse;
import com.javatechie.grpc.StockTradingServiceGrpc;
import com.javatechie.grpc.OrderSummary;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {

    /***
     * this is my business logic its already fully enabled make sure its working mode+
     * mapped with my backend logic+ status up {backend logic make sure this navigate us}
     */
    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceStub stockTradingServiceStub;

    /***
     *
     * @param symbol
     * business logic make sure we will handle its
     */

    public void subscribeStockPrice(String symbol) {

        StockRequest request = StockRequest.newBuilder()
                .setStockSymbol(symbol)
                .build();

        stockTradingServiceStub.subscribeStockPrice(request, new StreamObserver<StockResponse>() {

            @Override
            public void onNext(StockResponse response) {
                System.out.println("Stock Price Update: " +
                        response.getStockSymbol() +
                        " Price: " + response.getPrice() +
                        " Time: " + response.getTimestamp());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace(); //  better debugging
            }

            @Override
            public void onCompleted() {
                System.out.println("Stock stream completed!");
            }
        });
    }

    /***
     * we will be handle and managed driven mindsets :"
     * focus & persisted :"ytyui
     */

    public void placeBulkOrders() {

        StreamObserver<OrderSummary> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(OrderSummary summary) {
                System.out.println("Order Summary:");
                System.out.println("Total Orders: " + summary.getTotalOrders());
                System.out.println("Success: " + summary.getSuccessCount());
                System.out.println("Amount: $" + summary.getTotalAmount());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server completed response!");
            }
        };

        StreamObserver<StockOrder> requestObserver =
                stockTradingServiceStub.bulkStockOrder(responseObserver);

        try {
            requestObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("1")
                    .setStockSymbol("AAPL")
                    .setOrderType("BUY")
                    .setPrice(150.5)
                    .setQuantity(10)
                    .build());

            requestObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("2")
                    .setStockSymbol("GOOGL")
                    .setOrderType("SELL")
                    .setPrice(2700.0)
                    .setQuantity(5)
                    .build());

            requestObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("3")
                    .setStockSymbol("TSLA")
                    .setOrderType("BUY")
                    .setPrice(700.0)
                    .setQuantity(8)
                    .build());

            requestObserver.onCompleted();

        } catch (Exception ex) {
            requestObserver.onError(ex);
        }
    }
}
