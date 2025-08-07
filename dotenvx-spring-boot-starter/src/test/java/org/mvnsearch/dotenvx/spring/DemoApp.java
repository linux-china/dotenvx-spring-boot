package org.mvnsearch.dotenvx.spring;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApp {
    public static void main(String[] args) {
        SpringApplication.run(DemoApp.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(ApplicationContext appContext) {
        return args -> {
            System.out.println(appContext.getEnvironment().getProperty("nick"));
            System.out.println(appContext.getEnvironment().getProperty("nick2"));
        };
    }
}
