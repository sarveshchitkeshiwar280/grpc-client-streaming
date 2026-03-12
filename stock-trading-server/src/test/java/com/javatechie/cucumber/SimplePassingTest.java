package com.javatechie.cucumber;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class SimplePassingTest {
    
    @Test
    void testAlwaysPasses() {
        System.out.println("? Simple test is running!");
        assertTrue(true, "This test should always pass");
        System.out.println("? Test passed successfully!");
    }
    
    @Test
    void testBasicAssertion() {
        int result = 1 + 1;
        assertTrue(result == 2, "1 + 1 should equal 2");
        System.out.println("? Basic math test passed!");
    }
}
