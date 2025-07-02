package com.example.realtimedashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Ensure this is present if not already via other configurations
public class RealTimeDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealTimeDashboardApplication.class, args);
    }

}
