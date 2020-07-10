package com.hr.cachemanage.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by r.hu on 2019/4/8.
 */
public class DatetimeUtil {

  private static Logger LOGGER = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);


  private static final String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

  /**
   * The constant format_yyyyMMdd.
   */
  public static final String FORMAT_YYYYMMDD = "yyyy-MM-dd";
  /**
   * The constant format_yyyyMd.
   */
  public static final String FORMAT_YYYYMD = "yyyy-M-d";
  /**
   * The constant format_yyyyMMddHH.
   */
  public static final String FORMAT_YYYYMMDDHH = "yyyy-MM-dd HH";
  /**
   * The constant format_yyyyMMddHHmm.
   */
  public static final String FORMAT_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
  /**
   * The constant format_yyyyMMddHHmmss.
   */
  public static final String FORMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
  /**
   * The constant format_yyyyMMddHHmmssS.
   */
  public static final String FORMAT_YYYYMMDDHHMMSSS = "yyyy-MM-dd HH:mm:ss.s";

  public static final String FORMAT_YYYYMMDDHHMMSSSSS = "yyyy-MM-dd HH:mm:ss.sss";

  /**
   * Parse date date.
   *
   * @param date   the date
   * @param format the format
   * @return the date
   */
  public static Date parseDate(String date, String format) {
    if (StringUtils.isBlank(date) || StringUtils.isBlank(format)) {
      return null;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    try {
      return simpleDateFormat.parse(date);
    } catch (ParseException e) {
      LOGGER.warn(StackTraceUtil.getStackTrace(e));
    }
    return null;
  }

  /**
   * Parse date date.
   *
   * @param date the date
   * @return the date
   */
  public static Date parseDate(String date) {
    if (StringUtils.isBlank(date)) {
      return null;
    }
    if (date.contains("\\")) {
      date = date.replaceAll("\\\\", "-");
    }
    if (date.contains("/")) {
      date = date.replaceAll("/", "-");
    }
    String format = getFormatDate(date);
    return parseDate(date, format);
  }

  /**
   * Gets format date.
   *
   * @param date the date
   * @return the format date
   */
  public static String getFormatDate(String date) {
    String format = null;
    if (!StringUtils.isBlank(date)) {
      if (date.matches("\\d+-\\d+-\\d+")) {
        format = FORMAT_YYYYMMDD;
      } else if (date.matches("\\d+-\\d+-\\d+ \\d+:\\d+:\\d+")) {
        format = FORMAT_YYYYMMDDHHMMSS;
      } else if (date.matches("\\d+-\\d+-\\d+ \\d+")) {
        format = FORMAT_YYYYMMDDHH;
      } else if (date.matches("\\d+-\\d+-\\d+ \\d+:\\d+")) {
        format = FORMAT_YYYYMMDDHHMM;
      } else if (date.matches("\\d+-\\d+-\\d+ \\d+:\\d+:\\d+.\\d+")) {
        format = FORMAT_YYYYMMDDHHMMSSS;
      }
    }
    return format;
  }

  /**
   * Add date date.
   *
   * @param date     the date
   * @param dateType the date type
   * @param value    the value
   * @return the date
   */
  public static Date addDate(Date date, int dateType, int value) {
    Calendar calendar = getCalendar(date);
    calendar.add(dateType, value);
    return calendar.getTime();
  }

  /**
   * Format date by time millis string.
   *
   * @param timeMillis the time millis
   * @param format     the format
   * @return the string
   */
  public static String formatDateByTimeMillis(Long timeMillis, String format) {

    String ret = null;

    if (null != timeMillis && !StringUtils.isBlank(format)) {

      try {
        Date date = new Date();
        date.setTime(Long.valueOf(timeMillis));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        ret = simpleDateFormat.format(date);
      } catch (Exception e) {
        LOGGER.warn(e);
      }
    }

    return ret;

  }

  /**
   * Has child boolean.
   *
   * @param birthDay   the birth day
   * @param flightDate the flight date
   * @return the boolean
   */
  public static boolean hasChild(Date birthDay, Date flightDate) {
    Calendar flightDateCalendar1 = Calendar.getInstance();
    Calendar flightDateCalendar2 = Calendar.getInstance();
    flightDate = flightDate == null ? new Date() : flightDate;
    flightDateCalendar1.setTime(flightDate);
    flightDateCalendar2.setTime(flightDate);
    flightDateCalendar1.set(Calendar.YEAR, -2);
    flightDateCalendar2.set(Calendar.YEAR, -12);
    return birthDay.getTime() <= flightDateCalendar1.getTime().getTime() && birthDay.getTime() >=
        flightDateCalendar2.getTime().getTime();
  }

  /**
   * Format date string.
   *
   * @param date   the date
   * @param format the format
   * @return the string
   */
  public static String formatDate(Date date, String format) {
    if (null == date || StringUtils.isBlank(format)) {
      return null;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    try {
      return simpleDateFormat.format(date);
    } catch (Exception e) {
      LOGGER.warn(e);
    }
    return null;
  }

  /**
   * Gets week day.
   *
   * @param date the date
   * @return the week day
   */
  public static String getWeekDay(Date date) {
    if (null == date) {
      return null;
    }
    try {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
      if (w < 0) {
        w = 0;
      }
      return weekDays[w];
    } catch (Exception e) {
      LOGGER.warn(e);
    }
    return null;
  }

  /**
   * Date to calendar calendar.
   * 若date为空，返回空
   *
   * @param date the date
   * @return the calendar
   */
  public static Calendar dateToCalendar(Date date) {
    Calendar calendar = null;
    if (date != null) {
      calendar = Calendar.getInstance();
      calendar.setTime(date);
    }
    return calendar;
  }

  /**
   * Gets date.
   *
   * @param calendar the calendar
   * @return the date
   */
  public static Date getDate(Calendar calendar) {
    return calendar == null ? null : calendar.getTime();
  }

  /**
   * Gets calendar.
   * 若date为空，默认返回今天的日期
   * @param date the date
   * @return the calendar
   */
  public static Calendar getCalendar(Date date) {
    Calendar calendar = Calendar.getInstance();
    if (null != date) {
      calendar.setTime(date);
    }
    return calendar;
  }

  /**
   * Get calendar calendar.
   *
   * @param dateStr the date str
   * @param format  the format
   * @return the calendar
   */
  public static Calendar getCalendar(String dateStr, String format) {
    Date date = parseDate(dateStr, format);
    if (date != null) {
      return getCalendar(date);
    }
    return null;
  }

  /**
   * Gets now.
   *
   * @return the now
   */
  public static Date getNow() {
    return new Date();
  }

  public static String getNow(String format) {
    if (StringUtils.isBlank(format)) {
      return null;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    try {
      return simpleDateFormat.format(new Date());
    } catch (Exception e) {
      LOGGER.warn(e);
    }
    return null;
  }

  /**
   * Date to xml date xml gregorian calendar.
   *
   * @param date the date
   * @return the xml gregorian calendar
   */
  public static XMLGregorianCalendar dateToXmlDate(Date date) {
    if (null == date) {
      return null;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    DatatypeFactory dtf = null;
    try {
      dtf = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      LOGGER.warn(e);
    }
    if (null != dtf) {
      XMLGregorianCalendar dateType = dtf.newXMLGregorianCalendar();
      if (null != dateType) {
        dateType.setYear(cal.get(Calendar.YEAR));
        // 由于Calendar.MONTH取值范围为0~11,需要加1
        dateType.setMonth(cal.get(Calendar.MONTH) + 1);
        dateType.setDay(cal.get(Calendar.DAY_OF_MONTH));
        dateType.setHour(cal.get(Calendar.HOUR_OF_DAY));
        dateType.setMinute(cal.get(Calendar.MINUTE));
        dateType.setSecond(cal.get(Calendar.SECOND));
        return dateType;
      }
    }
    return null;
  }

  /**
   * Xml date to date date.
   *
   * @param calendar the calendar
   * @return the date
   */
  public static Date xmlDateToDate(XMLGregorianCalendar calendar) {
    Date date = null;
    if (calendar != null) {
      date = calendar.toGregorianCalendar().getTime();
    }
    return date;
  }
}

