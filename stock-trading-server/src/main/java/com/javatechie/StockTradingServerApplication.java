package com.javatechie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {
        "com.javatechie",
        "com.javatechie.controller",
        "com.javatechie.service",
        "com.javatechie.repository"  // Fixed typo: "com" not "om"
})
@RestController  // ADD THIS
public class StockTradingServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockTradingServerApplication.class, args);
    }

    // TEMPORARY TEST ENDPOINT - DELETE AFTER FIX
    @GetMapping("/")
    public String home() {
        return "Stock Trading Server is RUNNING! Time: " + new java.util.Date();
    }

    @GetMapping("/test")
    public String test() {
        return "Direct test endpoint works!";
    }
}