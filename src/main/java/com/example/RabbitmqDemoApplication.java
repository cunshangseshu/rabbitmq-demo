package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * RabbitMQ 学习项目的启动入口。
 *
 * @SpringBootApplication 组合了三个重要能力：
 *
 * 1. @Configuration
 *    表示当前类可以声明 Spring Bean。
 * 2. @EnableAutoConfiguration
 *    根据 pom.xml 中的依赖自动配置 Spring MVC、RabbitMQ、JPA 等组件。
 * 3. @ComponentScan
 *    扫描当前包以及子包中的 Controller、Service、Component 等类。
 *
 */
@SpringBootApplication
@EnableScheduling
public class RabbitmqDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(RabbitmqDemoApplication.class, args);
    }
}