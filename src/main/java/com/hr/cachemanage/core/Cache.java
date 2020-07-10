package com.hr.cachemanage.core;

import com.hr.cachemanage.annotation.CacheBean;
import java.lang.invoke.MethodHandle;
import java.util.Map;

public class Cache {

  // 缓存名称
  private String cacheName;

  // 缓存关联的 CacheBean
  private CacheBean cacheBean;

  // 缓存初始化方法是否包含参数
  private Map<String, Object[]> params;

  // 缓存内容数据
  private Map<String, Object> data;

  // 缓存第一次更新时间
  private long firstExecuteTime;

  // 缓存最后一次更新时间
  private long lastExecuteTime;

  // 缓存是否自动刷新
  private boolean autoRefresh;

  // 缓存自动刷新时间
  private long timeOut;
  private boolean containParam;

  // 缓存外部依赖
  private String[] dependentReference;

  // 缓存描述
  private String description;

  // 将方法和 bean 绑定 反射时调用
  private MethodHandle handler;

  // 是否是核心缓存
  private boolean essential;

  // 缓存重试次数
  private int retryTimes;

  public long getTimeOut() {
    return timeOut;
  }

  public void setTimeOut(long timeOut) {
    this.timeOut = timeOut;
  }

  public CacheBean getCacheBean() {
    return cacheBean;
  }

  public void setCacheBean(CacheBean cacheBean) {
    this.cacheBean = cacheBean;
  }

  public Map<String, Object[]> getParams() {
    return params;
  }

  public void setParams(Map<String, Object[]> params) {
    this.params = params;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  public long getFirstExecuteTime() {
    return firstExecuteTime;
  }

  public void setFirstExecuteTime(long firstExecuteTime) {
    this.firstExecuteTime = firstExecuteTime;
  }

  public long getLastExecuteTime() {
    return lastExecuteTime;
  }

  public void setLastExecuteTime(long lastExecuteTime) {
    this.lastExecuteTime = lastExecuteTime;
  }

  public boolean isAutoRefresh() {
    return autoRefresh;
  }

  public void setAutoRefresh(boolean autoRefresh) {
    this.autoRefresh = autoRefresh;
  }

  public String getCacheName() {
    return cacheName;
  }

  public void setCacheName(String cacheName) {
    this.cacheName = cacheName;
  }

  public boolean isContainParam() {
    return containParam;
  }

  public void setContainParam(boolean containParam) {
    this.containParam = containParam;
  }

  public String[] getDependentReference() {
    return dependentReference;
  }

  public void setDependentReference(String[] dependentReference) {
    this.dependentReference = dependentReference;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public MethodHandle getHandler() {
    return handler;
  }

  public void setHandler(MethodHandle handler) {
    this.handler = handler;
  }

  public boolean isEssential() {
    return essential;
  }

  public void setEssential(boolean essential) {
    this.essential = essential;
  }

  public int getRetryTimes() {
    return retryTimes;
  }

  public void setRetryTimes(int retryTimes) {
    this.retryTimes = retryTimes;
  }
}
