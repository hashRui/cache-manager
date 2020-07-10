package com.hr.cachemanage.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by r.hu on 2020/7/10.
 */
public final class Converter {
  /**
   * Parse int integer.
   *
   * @param from the from
   * @param def  the def
   * @return the integer
   */
  public static Integer parseInt(String from,boolean ... def){
    if(isNum(from)){
      return Integer.valueOf(from);
    }
    return def != null && def.length > 0 && def[0] ? 0 : null;
  }

  /**
   * Parse long long.
   *
   * @param from the from
   * @param def  the def
   * @return the long
   */
  public static Long parseLong(String from,boolean ... def){
    if(isNum(from)){
      return Long.valueOf(from);
    }
    return def != null && def.length > 0 && def[0] ? 0L : null;
  }

  /**
   * Is num boolean.
   *
   * @param from the from
   * @return the boolean
   */
  public static boolean isNum(String from){
    return from != null && from.matches("^[-+]?[0-9]+$");
  }

  /**
   * Is double boolean.
   *
   * @param from the from
   * @return the boolean
   */
  public static boolean isDouble(String from){
    boolean result = false;
    if (from!=null){
      result = from.matches("^[-+]?(\\d+(\\.\\d*)?|\\.\\d+)([eE]([-+]?([012]?\\d{1,2}|30[0-7])|-3([01]?[4-9]|[012]?[0-3])))?[dD]?$");
    }
    return result;
  }

  /**
   * Parse double double.
   *
   * @param from the from
   * @param def  the def
   * @return the double
   */
  public static Double parseDouble(String from,boolean ... def) {
    if (isDouble(from)){
      return Double.parseDouble(from);
    }
    return def != null && def.length > 0 && def[0] ? 0D : null;
  }

  /**
   * Parse float float.
   *
   * @param from the from
   * @param def  the def
   * @return the float
   */
  public static Float parseFloat(String from,boolean ... def) {
    if (isDouble(from)){
      return Float.parseFloat(from);
    }
    return def != null && def.length > 0 && def[0] ? 0F : null;
  }

  /**
   * Parse byte byte.
   *
   * @param from the from
   * @param def  the def
   * @return the byte
   */
  public static Byte parseByte(String from,boolean ... def) {
    if (isDouble(from)){
      return Byte.parseByte(from);
    }
    return def != null && def.length > 0 && def[0] ? Byte.parseByte("0") : null;
  }

  /**
   * Parse short short.
   *
   * @param from the from
   * @param def  the def
   * @return the short
   */
  public static Short parseShort(String from,boolean ... def) {
    if (isDouble(from)){
      return Short.parseShort(from);
    }
    return def != null && def.length > 0 && def[0] ? Short.parseShort("0") : null;
  }

  /**
   * Parse number t.
   *
   * @param <T>    the type parameter
   * @param value  the value
   * @param tClass the t class
   * @return the t
   */
  public static <T> T parseNumber(String value,Class<T> tClass){
    T result = null;
    if (tClass == Integer.class || tClass == int.class){
      result = (T)parseInt(value);
    }else if (tClass == Double.class || tClass == double.class){
      result = (T)parseDouble(value);
    }else if (tClass == Long.class || tClass == long.class){
      result = (T)parseLong(value);
    }else if (tClass == Short.class || tClass == short.class){
      result = (T)parseShort(value);
    }else if (tClass == Byte.class || tClass == byte.class){
      result = (T)parseByte(value);
    }else if (tClass == Float.class || tClass == float.class){
      result = (T)parseFloat(value);
    }else if (tClass == Character.class || tClass == char.class){
      result = (T) parseChar(value);
    }
    return result;
  }

  public static Character parseChar(String value){
    if (!StringUtils.isBlank(value) && value.length() == 1){
      return value.charAt(0);
    }
    return null;
  }

}
