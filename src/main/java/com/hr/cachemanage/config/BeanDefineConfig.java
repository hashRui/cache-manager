package com.hr.cachemanage.config;

import com.hr.cachemanage.core.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by r.hu on 2019/4/8.
 */
@Component("beanDefineConfig")
public class BeanDefineConfig implements ApplicationListener<ContextRefreshedEvent> {
  @Autowired
  private CacheManager cacheManager;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // spring 加载完所有 bean 的时候初始化缓存
    cacheManager.initCache();
  }
}
