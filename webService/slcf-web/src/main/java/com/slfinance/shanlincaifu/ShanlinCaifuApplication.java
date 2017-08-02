package com.slfinance.shanlincaifu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.slfinance.shanlincaifu.limits.RequestLimitsManager;
import com.slfinance.spring.ServiceLocator;
import com.slfinance.util.MetaDataManager;

@SpringBootApplication
@EnableJpaRepositories
@ImportResource("classpath*:/applicationContext-*.xml")
public class ShanlinCaifuApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShanlinCaifuApplication.class, args);
    }
    
    @Bean 
    MetaDataManager metaDataManager() {
    	return new MetaDataManager(new String[] {"com.slfinance.shanlincaifu.controller"});
    }
    
    @Bean
    RequestLimitsManager requestLimitsManager() {
    	return new RequestLimitsManager(new String[] {"com.slfinance.shanlincaifu.service"});
    }
    
    @Bean
    ServiceLocator serviceLocator() {
    	return new ServiceLocator();
    }
}
