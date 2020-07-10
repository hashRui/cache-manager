package com.hr.cachemanage.config;

import com.hr.cachemanage.core.CacheManager;
import com.hr.cachemanage.enums.SpringUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by r.hu on 2020/7/10
 */
@Configuration
public class SpringConfig {

    @Bean
    public CacheManager cacheManager(ApplicationContext context){
        CacheManager onlineCacheManager = new CacheManager();
        onlineCacheManager.setApplicationContext(context);
        return onlineCacheManager;
    }

    @Bean
    public SpringUtil getSpringUtil(){
        return SpringUtil.UTIL;
    }

}
