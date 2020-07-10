package com.hr.cachemanage.controller;

import com.hr.cachemanage.core.Cache;
import com.hr.cachemanage.core.CacheManager;
import com.hr.cachemanage.util.DatetimeUtil;
import com.hr.cachemanage.util.JacksonUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by r.hu on 2020/7/10.
 */
@Controller
@RequestMapping("/cache")
public class CacheController {

  private CacheManager cacheManager;

  @Autowired
  public CacheController(CacheManager cacheManager) {
    this.cacheManager = cacheManager;

  }

  @RequestMapping("getAllCaches")
  @ResponseBody
  public String getAllCaches() {
    return null;
  }

  @RequestMapping("getCache")
  @ResponseBody
  public Object getCache(String key) {
    Object o = cacheManager.getCache(key);
    if (o instanceof String){
      return o;
    }

    return JacksonUtil.toJson(o);
  }

  @RequestMapping("refresh")
  @ResponseBody
  public String refresh(String key) {
    Boolean result;
    if (key != null) {
      result = cacheManager.refresh(key);
    } else {
      result = cacheManager.refresh();
    }
    return result ? "Success" : "Failure";
  }

  @RequestMapping("getCacheKeys")
  @ResponseBody
  public String getCacheKeys() {
    Map<String, Cache> cacheMap = cacheManager.getAllCache();
    Map<String, Map<String, Object>> result = new HashMap<>();
    for (String s : cacheMap.keySet()) {
      Cache cache = cacheMap.get(s);
      Map<String, Object> map = new HashMap<>();
      map.put("cacheKeys", cache.getData().keySet());
      map.put("description", cache.getDescription());
      map.put("firstExecuteTime",
          DatetimeUtil.formatDate(new Date(cache.getFirstExecuteTime()), "yyyy-MM-dd HH:mm:ss.s"));
      map.put("LastExecuteTime",
          DatetimeUtil.formatDate(new Date(cache.getLastExecuteTime()), "yyyy-MM-dd HH:mm:ss.s"));
      result.put(s, map);
    }
    return JacksonUtil.toJson(result);
  }
}
