package com.accenture.hr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class HrApplication {

    @Value("${maxCapacity}")
    private int maxCapacity;
    @Value("${currentAllowancePercent}")
    private int currentAllowancePercent;

    public static void main(String[] args) {
        SpringApplication.run(HrApplication.class, args);
    }

    @Bean
    public Map<Long, LocalDateTime> peopleInside() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<Long, LocalDateTime> peopleWaiting() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Integer currentLimit() {
        return (int) (maxCapacity * ((double) currentAllowancePercent / 100));
    }

}
