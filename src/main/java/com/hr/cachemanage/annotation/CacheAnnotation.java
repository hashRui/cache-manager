package com.hr.cachemanage.annotation;

import com.hr.cachemanage.util.CacheTimeType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheAnnotation {

  // 缓存名称
  String name();

  // 缓存自动刷新的时间单位
  CacheTimeType timeType() default CacheTimeType.Minute;

  // 缓存自动刷新时间
  int timeOut() default 30;

  // 缓存是否自动刷新，默认为 true ,则超过自动刷新时间则去刷新缓存。
  boolean autoFlash() default true;

  // 缓存之间是否存在依赖
  String[] dependentReference() default {};

  // 缓存说明
  String description() default "这个人很懒，没有填缓存说明";

  // 该缓存是否是必须的, 核心缓存初始化会影响项目启动，非核心缓存默认为false
  boolean essential() default false;

  // 缓存初始化失败,重试次数
  int retryTimes() default 0;
}
