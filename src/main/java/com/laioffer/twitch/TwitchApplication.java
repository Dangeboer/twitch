package com.laioffer.twitch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class TwitchApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitchApplication.class, args); // 最大的入口，基本不用在这里写代码
    }

}
