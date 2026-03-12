package com.javatechie.service;


import com.javatechie.entity.Stock;
import com.javatechie.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found with symbol: " + symbol));
    }

    public Stock createStock(Stock stock) {
        if (stockRepository.existsBySymbol(stock.getSymbol())) {
            throw new RuntimeException("Stock with symbol " + stock.getSymbol() + " already exists");
        }
        return stockRepository.save(stock);
    }

    public Stock updateStockPrice(String symbol, double newPrice) {
        Stock stock = getStockBySymbol(symbol);
        stock.setPrice(newPrice);
        return stockRepository.save(stock);
    }

    public void deleteStock(String symbol) {
        Stock stock = getStockBySymbol(symbol);
        stockRepository.delete(stock);
    }

    public List<Stock> getStocksByPriceRange(double minPrice, double maxPrice) {
        return stockRepository.findAll().stream()
                .filter(stock -> stock.getPrice() >= minPrice && stock.getPrice() <= maxPrice)
                .toList();
    }
}