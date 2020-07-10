package com.hr.cachemanage.entity;


import java.io.Serializable;

/**
 * The type City entity.
 * Created by @author: hurui
 * Created on @date: 2020.07.10
 */
public class CityEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  // 城市三字码
  private String code;

  // 所在国家
  private Integer countryId;

  // 所在国家
  private String countryName;

  // 城市ID
  private Integer id;

  // 城市名称
  private String name;

  // 城市英文名称
  private String nameEn;

  // 城市名称拼音
  private String namePinyin;

  // 所在省
  private Integer provinceId;

  public CityEntity() {
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Integer getCountryId() {
    return countryId;
  }

  public void setCountryId(Integer countryId) {
    this.countryId = countryId;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getNamePinyin() {
    return namePinyin;
  }

  public void setNamePinyin(String namePinyin) {
    this.namePinyin = namePinyin;
  }

  public Integer getProvinceId() {
    return provinceId;
  }

  public void setProvinceId(Integer provinceId) {
    this.provinceId = provinceId;
  }

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

}
