package com.hr.cachemanage.enums;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Created by @author: qydai
 * Created on @date: 2019/4/12
 */
public enum SpringUtil {
  /**
   * Util online spring utils.
   */
  UTIL;

  @Autowired
  private ApplicationContext applicationContext;
  /**
   * 根据bean的id来查找对象
   *
   * @param id the id
   * @return bean by id
   */
  public Object getBeanById(String id) {
    return applicationContext.getBean(id);
  }

  /**
   * Gets bean by name.
   *
   * @param <T>  the type parameter
   * @param name the name
   * @param c    the c
   * @return the bean by name
   */
  public <T> T getBeanByName(String name, Class<T> c) {
    return applicationContext.getBean(name, c);
  }

  /**
   * 根据bean的class来查找对象
   *
   * @param <T> the type parameter
   * @param c   the c
   * @return bean by class
   */
  public <T> T getBeanByClass(Class<T> c) {
    return (T) applicationContext.getBean(c);
  }

  /**
   * 根据bean的class来查找所有的对象(包括子类)
   *
   * @param c the c
   * @return beans by class
   */
  public <T> Map<String, T> getBeansByClass(Class<T> c) {
    return applicationContext.getBeansOfType(c);
  }
}
