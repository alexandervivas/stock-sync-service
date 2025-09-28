package com.upwork.vendor.b;

import com.upwork.vendor.b.config.VendorBProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties(VendorBProperties.class)
@SpringBootApplication
public class VendorBApplication {
    public static void main(String[] args) {
        SpringApplication.run(VendorBApplication.class, args);
    }
}
