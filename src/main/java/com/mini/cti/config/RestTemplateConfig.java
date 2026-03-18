package com.mini.cti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return  new RestTemplate();
    }

//    @Bean
//    public RestTemplate cisaRestTemplate(){
//        HttpComponentsClientHttpRequestFactory factory =
//                new HttpComponentsClientHttpRequestFactory();
//        factory.setConnectionRequestTimeout(30000);
//        factory.setReadTimeout(60000);
//
//        return new RestTemplate(factory);
//    }

}
