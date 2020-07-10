package com.hr.cachemanage.cachehelper;


import com.hr.cachemanage.annotation.CacheAnnotation;
import com.hr.cachemanage.annotation.CacheBean;
import com.hr.cachemanage.core.CacheManager;
import com.hr.cachemanage.entity.CityEntity;
import com.hr.cachemanage.entity.SoaCityEntity;
import com.hr.cachemanage.util.LocalCacheConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 城市缓存 Created by r.hu on 2020/7/10
 */
@Component
public class CityCacheHelper implements CacheBean {

    Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);


    @Autowired
    CacheManager cacheManager;

    /**
     * 从 cacheManager 中 通过 key code_to_city_map 获取所有的城市信息
     */
    public Map<String, CityEntity> getAllCities() {
        return cacheManager.getCache(LocalCacheConstant.CODE_TO_CITY_MAP);
    }

    /**
     * 缓存初始化 1、cacheManager 扫描到注解,按照注解里的配置执行该方法 2、按照 缓存的 name 缓存数据为 value 放到 map 中
     * 3、将需要自动刷新的数据放入延时线程池
     */
    @CacheAnnotation(name = LocalCacheConstant.CODE_TO_CITY_MAP, timeOut = 120, essential = true, retryTimes = 3)
    public Map<String, CityEntity> initCodeToCityMap() {
        Map<String, CityEntity> map = null;
        // get result from db or soa
        List<SoaCityEntity> cities = getAllCitiesFromSoa();
        if (!CollectionUtils.isEmpty(cities)) {
            map = new HashMap<>(cities.size());
            for (SoaCityEntity soaEntity : cities) {
                if (StringUtils.isBlank(soaEntity.getCode()) || map
                    .containsKey(soaEntity.getCode().trim
                        ())) {
                    continue;
                }
                CityEntity cacheEntity = new CityEntity();
                cacheEntity.setCode(soaEntity.getCode());
                cacheEntity.setCountryId(soaEntity.getCountryID());
                cacheEntity.setId(soaEntity.getId());
                cacheEntity.setName(soaEntity.getName());
                cacheEntity.setNameEn(soaEntity.getName_En());
                cacheEntity.setNamePinyin(soaEntity.getNamePinyin());
                cacheEntity.setProvinceId(soaEntity.getProvinceID());
                cacheEntity.setCountryName(soaEntity.getCountryName());

                map.put(cacheEntity.getCode().trim().toUpperCase(), cacheEntity);
            }
        }
        return map;
    }

    public Map<Integer, CityEntity> getAllCitiesIdMap() {
        return cacheManager.getCache(LocalCacheConstant.ID_TO_CITY_MAP);
    }

    /**
     * cache init
     */
    @CacheAnnotation(name = LocalCacheConstant.ID_TO_CITY_MAP, timeOut = 15, essential = true, retryTimes = 3)
    public Map<Integer, CityEntity> initIdToCityMap() {
        Map<Integer, CityEntity> map = null;
        List<SoaCityEntity> cities = getAllCitiesFromSoa();
        if (cities != null && !cities.isEmpty()) {
            map = new HashMap<>();
            for (SoaCityEntity soaEntity : cities) {
                if (map.containsKey(soaEntity.getId())) {
                    continue;
                }
                CityEntity cacheEntity = new CityEntity();
                cacheEntity.setCode(soaEntity.getCode());
                cacheEntity.setCountryId(soaEntity.getCountryID());
                cacheEntity.setId(soaEntity.getId());
                cacheEntity.setName(soaEntity.getName());
                cacheEntity.setNameEn(soaEntity.getName_En());
                cacheEntity.setNamePinyin(soaEntity.getNamePinyin());
                cacheEntity.setProvinceId(soaEntity.getProvinceID());
                map.put(cacheEntity.getId(), cacheEntity);
            }
        }
        return map;
    }

    public Map<String, CityEntity> getAllCitiesNameMap() {
        return cacheManager.getCache(LocalCacheConstant.NAME_TO_CITY_MAP);
    }

    /**
     * get cache
     */
    @CacheAnnotation(name = LocalCacheConstant.NAME_TO_CITY_MAP, timeOut = 15, essential = true, retryTimes = 3)
    public Map<String, CityEntity> initNameToCityMap() {
        Map<String, CityEntity> map = null;
        List<SoaCityEntity> cities = getAllCitiesFromSoa();
        if (cities != null && !cities.isEmpty()) {
            map = new HashMap<>();
            for (SoaCityEntity soaEntity : cities) {
                if (map.containsKey(soaEntity.getId())) {
                    continue;
                }
                CityEntity cacheEntity = new CityEntity();
                cacheEntity.setCode(soaEntity.getCode());
                cacheEntity.setCountryId(soaEntity.getCountryID());
                cacheEntity.setId(soaEntity.getId());
                cacheEntity.setName(soaEntity.getName());
                cacheEntity.setNameEn(soaEntity.getName_En());
                cacheEntity.setNamePinyin(soaEntity.getNamePinyin());
                cacheEntity.setProvinceId(soaEntity.getProvinceID());
                map.put(cacheEntity.getName(), cacheEntity);
            }
        }
        return map;
    }

    /**
     * 从接口获取所有城市信息
     */
    private List<SoaCityEntity> getAllCitiesFromSoa() {
        // get result from db or soa
        logger.info("从soa接口获取缓存数据");
        List<SoaCityEntity> cityEntities = new ArrayList<>();
        SoaCityEntity soaCityEntity = new SoaCityEntity();
        soaCityEntity.setCode("SHA");
        soaCityEntity.setCountryCode("1");
        soaCityEntity.setCountryEname("中国");
        soaCityEntity.setCountryID(1);
        soaCityEntity.setFltCityEname("shanghai");
        soaCityEntity.setFltCityName("上海");
        soaCityEntity.setHtlCityID(2);
        soaCityEntity.setId(2);
        soaCityEntity.setProvinceID(1);
        soaCityEntity.setNamePinyin("shanghai");
        soaCityEntity.setName_En("shanghai");
        soaCityEntity.setName("上海");
        cityEntities.add(soaCityEntity);
        return cityEntities;
    }
}
