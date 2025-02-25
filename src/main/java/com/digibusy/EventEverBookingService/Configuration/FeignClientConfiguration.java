package com.digibusy.EventEverBookingService.Configuration;

import feign.Feign;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {
//    @Bean
//    public EventFeignClient eventFeignClient() {
//        return Feign.builder()
//                .target(EventFeignClient.class, "http://localhost:8081");
//    }
//    @Bean
//    public Request.Options requestOptions(){
//        return new Request.Options(5000,5000,true);
//    }
}
