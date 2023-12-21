package com.douyin.service;

import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan(basePackages = "com.douyin.service.mapper")
//@MapperScan(basePackages = "com.douyin.service.repository")
@ComponentScan(basePackages = {"com.douyin", "org.n3r.idworker"})
@EnableMongoRepositories
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
