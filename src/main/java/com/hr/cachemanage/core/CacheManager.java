package com.hr.cachemanage.core;

import com.hr.cachemanage.annotation.CacheAnnotation;
import com.hr.cachemanage.annotation.CacheBean;
import com.hr.cachemanage.util.CacheTimeType;
import com.hr.cachemanage.util.HrCollectionUtil;
import com.hr.cachemanage.util.StackTraceUtil;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.CollectionUtils;

/**
 * Created by r.hu on 2020/7/10
 */
public class CacheManager {

    Logger LOGGER = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 储存所有缓存的 map
    private Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    // jdk 的延时线程池
    private ScheduledThreadPoolExecutor executor;
    private Object lock = new Object();


    private List<Runnable> executeRunnableList = new ArrayList<>();
    private boolean isInit = true;

    public synchronized void initCache() {
        try {
            if (executor == null) {
                // 初始化线程池  核心线程数设置为 CPU的核数
                executor = (ScheduledThreadPoolExecutor) Executors
                    .newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
            }
            // 如果是刷新需要首先清理缓存 Map 和 任务List
            cacheMap.clear();
            executeRunnableList.clear();
            // 获取所有实现了 CacheBean 接口的类
            Map<String, CacheBean> map = applicationContext.getBeansOfType(CacheBean.class);
            Collection<CacheBean> beans = map.values();
            for (CacheBean bean : beans) {
                // 获取所有实现了 CacheAnnotation 注解的方法
                List<Method> methods = HrCollectionUtil.findAll(
                    Arrays.asList(bean.getClass().getMethods()),
                    x -> x.getAnnotation(CacheAnnotation.class) != null);
                for (Method method : methods) {
                    // 为每个 CacheAnnotation 新建一个 Cache
                    Cache cache = new Cache();
                    CacheAnnotation cacheAnnotation = method.getAnnotation(CacheAnnotation.class);
                    // 获取方法的参数
                    Parameter[] parameters = method.getParameters();
                    // 判断 是否存在参数
                    cache.setContainParam(parameters != null && parameters.length > 0);
                    // set 是否自动刷新
                    cache.setAutoRefresh(cacheAnnotation.autoFlash());
                    // set 当前的 CacheBean
                    cache.setCacheBean(bean);
                    // set 缓存名称
                    cache.setCacheName(cacheAnnotation.name());
                    // 计算缓存超时自动刷新时间
                    cache.setTimeOut(
                        getTimeOut(cacheAnnotation.timeOut(), cacheAnnotation.timeType()));
                    // set 缓存数据初始值
                    cache.setData(new ConcurrentHashMap<>());
                    // 因为编译时还未获取到实际参数，暂时给参数设置一个空
                    cache.setParams(new ConcurrentHashMap<>());
                    // set 缓存描述
                    cache.setDescription(cacheAnnotation.description());
                    // 将 method 和 bean 封装成 handle
                    cache.setHandler(convertHandler(method, bean));
                    // set 缓存是否存在外部依赖
                    cache.setDependentReference(cacheAnnotation.dependentReference() != null
                        && cacheAnnotation.dependentReference().length > 0 ? cacheAnnotation
                        .dependentReference() : null);
                    // set 是否是必须的核心缓存数据
                    cache.setEssential(cacheAnnotation.essential());
                    // set 重试次数
                    cache.setRetryTimes(cacheAnnotation.retryTimes());
                    cacheMap.put(cacheAnnotation.name(), cache);
                }
            }
            // 根据缓存外部依赖对 key 进行排序，被依赖的缓存优先加载数据
            List<String> keyList = sortKey();
            for (String key : keyList) {
                Cache cache = cacheMap.get(key);
                // 执行获取缓存数据方法
                executeSaveCache(cache);
                // 如果缓存是自动刷新，给缓存交给延时线程池 相当于定时任务来定时刷新
                if (cache.isAutoRefresh()) {
                    Runnable runnable = () -> executeSaveCache(cache);
                    executor.scheduleAtFixedRate(runnable, cache.getTimeOut(),
                        cache.getTimeOut(), TimeUnit.MILLISECONDS);
                    executeRunnableList.add(runnable);
                }
            }
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), StackTraceUtil.getStackTrace(e));
        }
    }

    /*
     * 频繁通过反射会导致内存泄漏，通过方法绑定避免内存泄漏
     */
    private MethodHandle convertHandler(Method method, CacheBean bean) {
        try {
            return MethodHandles.lookup().unreflect(method)
                .bindTo(bean);

        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    /*
     * 重新排序 key ，将依赖其他缓存的 key 排到前面
     */
    private List<String> sortKey() {
        LinkedList<String> linkedList = new LinkedList<>(cacheMap.keySet());
        Collection<Cache> caches = cacheMap.values();
        if (!HrCollectionUtil.isNullOrEmpty(linkedList)) {
            for (Cache cache : caches) {
                if (cache.getDependentReference() != null) {
                    int cIndex = linkedList.indexOf(cache.getCacheName());
                    int refIndex = 0;
                    // 获取 缓存依赖 key 的最小索引
                    for (String s : cache.getDependentReference()) {
                        int index = linkedList.indexOf(s);
                        if (index > refIndex) {
                            refIndex = index;
                        }
                    }
                    // 如果缓存的 key 索引在 依赖索引之前，则将缓存索引放到依赖索引之后
                    if (cIndex < refIndex) {
                        linkedList.add(refIndex + 1, cache.getCacheName());
                        linkedList.remove(cIndex);
                    }
                }
            }
        }
        return linkedList;
    }

    /*
     * 获取初始化缓存数据执行，缓存刷新数据执行
     */
    private void executeSaveCache(Cache cache) {
        try {
            // 记录缓存的开始执行时间
            long executeBegin = System.currentTimeMillis();
            MethodHandle method = cache.getHandler();

            if (!CollectionUtils.isEmpty(cache.getData())) {
                isInit = false;
            }
            Object result = null;
            // 如果缓存执行方法不包含参数，则直接执行
            if (!cache.isContainParam()) {
                try {
                    result = method.invoke();
                } catch (Exception e) {
                    LOGGER.error(
                        "获取缓存失败CacheManager.executeSaveCache. CacheName: " + cache.getCacheName(),
                        StackTraceUtil.getStackTrace(e));
                }
                if (isInit && null == result && cache.isEssential() && cache.getRetryTimes() > 0) {
                    for (int i = 0; i < cache.getRetryTimes(); i++) {
                        try {
                            result = method.invoke();
                        } catch (Exception e) {
                            LOGGER.error(
                                "重试获取缓存失败CacheManager.executeSaveCache. CacheName: " + cache
                                    .getCacheName()
                                    + ", 重试次数: " + i + 1, StackTraceUtil.getStackTrace(e));
                        }
                        if (null != result) {
                            break;
                        }
                    }
                    if (null == result) {
                        LOGGER.fatal("无法获取强依赖缓存数据，系统主动退出，点火失败。",
                            "CacheName: " + cache.getCacheName());
                        ((ConfigurableApplicationContext) applicationContext).close();
                        throw new Exception(
                            "无法获取强依赖缓存数据，系统主动退出，点火失败。CacheName: " + cache.getCacheName());
                    }
                }
                if (result != null) {
                    cache.getData().put(cache.getCacheName(), result);
                    long nowTime = System.currentTimeMillis();
                    // set 缓存执行 fist and last 时间
                    if (cache.getFirstExecuteTime() <= 0) {
                        cache.setFirstExecuteTime(nowTime);
                    }
                    cache.setLastExecuteTime(nowTime);
                } else {
                    LOGGER.error("获取缓存数据失败", "缓存名: " + cache.getCacheName());
                }
            } else {
                // 如果缓存执行方法拿到参数，则执行。项目启动初始化的时候，参数为空，所以不会执行
                if (!cache.getParams().isEmpty()) {
                    // 为所有缓存参数执行缓存刷新
                    for (String paramKey : cache.getParams().keySet()) {
                    result = method.invokeWithArguments(cache.getParams().get(paramKey));
                    cache.getData().put(paramKey, result);
                    cache.setFirstExecuteTime(System.currentTimeMillis());
                    long nowTime = System.currentTimeMillis();
                    if (cache.getFirstExecuteTime() <= 0) {
                        cache.setFirstExecuteTime(nowTime);
                    }
                    cache.setLastExecuteTime(nowTime);
                }
            }
            }
            long executeEnd = System.currentTimeMillis();
            LOGGER.info("localCache", "本地缓存：" + cache.getCacheName()
                + "刷新，耗时：" + (executeEnd - executeBegin) + "毫秒");
        } catch (Throwable e) {
            LOGGER.error("执行缓存任务失败", StackTraceUtil.getStackTrace(e));
        }
    }

    private int getTimeOut(int i, CacheTimeType cacheTimeType) {
        int timeOut;
        switch (cacheTimeType) {
            case Day:
                timeOut = i * 1000 * 60 * 60 * 24;
                break;
            case Hour:
                timeOut = i * 1000 * 60 * 60;
                break;
            case Minute:
                timeOut = i * 1000 * 60;
                break;
            case Second:
                timeOut = i * 1000;
                break;
            default:
                timeOut = i;
        }
        return timeOut;
    }


    /*
     * 暴露给调用方获取缓存的接口
     */
    public <T> T getCache(String cacheName, Object... params) {
        T t = null;
        try {
            // 如果 CacheMap 中不存在 Key 直接返回 NULL
            if (cacheMap.containsKey(cacheName)) {
                Cache cache = cacheMap.get(cacheName);
                long nowTime = System.currentTimeMillis();
                // 缓存中是否包含参数
                if (!cache.isContainParam()) {
                    // 如果缓存是自动刷新，则直接从缓存中返回结果，否者调用 getStaticCache
                    if (cache.isAutoRefresh()) {
                        t = (T) cache.getData().get(cacheName);
                    } else {
                        t = getStaticCache(cacheName, cache, nowTime);
                    }
                } else {
                    // 如果有参数，则缓存 Key 为 CacheName + params ,然后调用 getStaticCache
                    if (params != null && params.length > 0) {
                        StringBuilder cacheKey = new StringBuilder(cacheName);
                        for (Object o : params) {
                            cacheKey.append(o.hashCode());
                        }
                        cache.getParams().put(cacheKey.toString(), params);
                        t = getStaticCache(cacheKey.toString(), cache, nowTime);
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.error(e);

        }
        return t;
    }

    private <T> T getStaticCache(String cacheKey, Cache cache, long nowTime) {
        T t = null;
        // 如果缓存在过期时间内，直接返回结果
        if (cache.getData().containsKey(cacheKey) && (nowTime - cache.getLastExecuteTime() <= cache
            .getTimeOut())) {
            t = (T) cache.getData().get(cacheKey);
            cache.setLastExecuteTime(nowTime);
        } else {
            // 添加同步锁，每次只允许一个方法调用
            synchronized (lock) {
                if (cache.getData().containsKey(cacheKey) && (nowTime - cache.getLastExecuteTime()
                    <= cache
                    .getTimeOut())) {
                    return (T) cache.getData().get(cacheKey);
                }
                MethodHandle method = cache.getHandler();
                try {
                    // 不带参数和带参数方法真实执行
                    if (cache.getParams().isEmpty()) {
                        t = (T) method.invoke();
                    } else {
                        t = (T) method.invokeWithArguments(cache.getParams().get(cacheKey));
                    }
                    if (cache.getFirstExecuteTime() <= 0) {
                        cache.setFirstExecuteTime(nowTime);
                    }
                    cache.setLastExecuteTime(nowTime);
                    // 将结果放入缓存中
                    cache.getData().put(cacheKey, t);
                } catch (Throwable e) {
                    LOGGER.error("getStaticCache", e);
                }
            }
        }
        return t;
    }

    /*
     * 调用 initCache 手动刷新所有缓存
     */
    public synchronized boolean refresh() {
        try {
            for (Runnable runnable : executeRunnableList) {
                executor.remove(runnable);
            }
            initCache();
        } catch (Throwable e) {
            LOGGER.error("refresh", e);
            return false;
        }
        return true;
    }

    /*
     * 手动刷新指定缓存
     */
    public synchronized boolean refresh(String key) {
        boolean result = true;
        long nowTime = System.currentTimeMillis();
        if (cacheMap.containsKey(key)) {
            Cache cache = cacheMap.get(key);
            cache.getData().clear();
            cache.getParams().clear();
            try {
                if (!cache.isContainParam()) {
                    cache.getData().put(key, cache.getHandler().invoke());
                }
            } catch (Throwable e) {
                LOGGER.error(e);
                result = false;
            }
            cache.setFirstExecuteTime(nowTime);
            cache.setLastExecuteTime(nowTime);
        }
        return result;
    }

    public Map<String, Cache> getAllCache() {
        return cacheMap;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
