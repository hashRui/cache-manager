package com.hr.cachemanage.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.util.CollectionUtils;

/**
 * The type  collection util.
 * Created by @auther: hurui
 * Created on @date: 2020.07.09
 */

public abstract class HrCollectionUtil {
  /**
   * Sort by value map.
   *
   * @param <K>        the type parameter
   * @param <V>        the type parameter
   * @param map        the map
   * @param comparator the comparator
   * @return the map
   */
  public static <K, V> Map<K, V> sortByValue(Map<K, V> map, Comparator<Map.Entry<K, V>>
      comparator) {
    return map.entrySet()
        .stream()
        .sorted(comparator)
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
  }

  /**
   * Create lru map map.
   *
   * @param <K>        the type parameter
   * @param <V>        the type parameter
   * @param maxEntries the max entries
   * @return the map
   */
  public static <K, V> Map<K, V> createLruMap(final int maxEntries) {
    return new LinkedHashMap<K, V>(maxEntries) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxEntries;
      }
    };
  }

  /**
   * Int to long list.
   *
   * @param list the list
   * @return the list
   */
  public static List<Long> intToLong(List<Integer> list) {
    List<Long> result = new ArrayList<>();
    if (!CollectionUtils.isEmpty(list)) {
      Function<Integer, Long> converter = item -> Long.parseLong(String.valueOf(item));
      result = list.stream().map(converter).collect(Collectors.toList());
    }
    return result;
  }

  /**
   * Long to integer list.
   *
   * @param list the list
   * @return the list
   */
  public static List<Integer> longToInteger(List<Long> list) {
    List<Integer> result = new ArrayList<>();
    if (!CollectionUtils.isEmpty(list)) {
      Function<Long, Integer> converter = item -> Integer.parseInt(String.valueOf(item));
      result = list.stream().map(converter).collect(Collectors.toList());
    }
    return result;
  }

  /**
   * Is null or empty boolean.
   *
   * @param <T>        the type parameter
   * @param collection the collection
   * @return the boolean
   */
  public static <T> boolean isNullOrEmpty(Collection<T> collection) {
    boolean result = false;
    if (collection == null || collection.isEmpty()) {
      result = true;
    }
    return result;
  }

  /**
   * Is null or empty boolean.
   *
   * @param <Key>   the type parameter
   * @param <Value> the type parameter
   * @param map     the map
   * @return the boolean
   */
  public static <Key, Value> boolean isNullOrEmpty(Map<Key, Value> map) {
    boolean result = false;
    if (map == null || map.isEmpty()) {
      result = true;
    }
    return result;
  }

  /**
   * Find first t.
   *
   * @param <T>        the type parameter
   * @param collection the collection
   * @param predicate  the predicate
   * @return the t
   */
  public static <T> T findFirst(Collection<T> collection, Predicate<T> predicate) {
    T t = null;
    if (!isNullOrEmpty(collection)) {
      Optional<T> optional = collection.stream().filter(predicate).findFirst();
      t = optional.orElse(null);
    }
    return t;
  }

  /**
   * Sum int integer.
   *
   * @param <T>        the type parameter
   * @param collection the collection
   * @param mapper     the mapper
   * @return the integer
   */
  public static <T> Integer sumInt(Collection<T> collection, ToIntFunction<? super T> mapper) {
    Integer number = null;
    if (!isNullOrEmpty(collection) && mapper != null) {
      number = collection.stream().collect(Collectors.summingInt(mapper));
    }
    return number;
  }

  /**
   * Sum long long.
   *
   * @param <T>        the type parameter
   * @param collection the collection
   * @param mapper     the mapper
   * @return the long
   */
  public static <T> Long sumLong(Collection<T> collection, ToLongFunction<? super T> mapper) {
    Long number = null;
    if (!isNullOrEmpty(collection) && mapper != null) {
      number = collection.stream().collect(Collectors.summingLong(mapper));
    }
    return number;
  }

  /**
   * Sum double double.
   *
   * @param <T>        the type parameter
   * @param collection the collection
   * @param mapper     the mapper
   * @return the double
   */
  public static <T> Double sumDouble(Collection<T> collection, ToDoubleFunction<? super T> mapper) {
    Double number = null;
    if (!isNullOrEmpty(collection) && mapper != null) {
      number = collection.stream().collect(Collectors.summingDouble(mapper));
    }
    return number;
  }

  /**
   * Min t.
   *
   * @param <T>        the type parameter
   * @param collection the collection
   * @param comparator the comparator
   * @return the t
   */
  public static <T> T min(Collection<T> collection, Comparator<? super T> comparator) {
    T t = null;
    if (!isNullOrEmpty(collection) && comparator != null) {
      Optional<T> optional = collection.stream().min(comparator);
      t = optional.isPresent() ? optional.get() : null;
    }
    return t;
  }

  /**
   * Min t.
   *
   * @param <T>        the type parameter
   * @param stream     the stream
   * @param comparator the comparator
   * @return the t
   */
  public static <T> T min(Stream<T> stream, Comparator<? super T> comparator) {
    T t = null;
    if (stream != null && comparator != null) {
      Optional<T> optional = stream.min(comparator);
      t = optional.isPresent() ? optional.get() : null;
    }
    return t;
  }

  /**
   * Max t.
   *
   * @param <T>        the type parameter
   * @param collection the collection
   * @param comparator the comparator
   * @return the t
   */
  public static <T> T max(Collection<T> collection, Comparator<? super T> comparator) {
    T t = null;
    if (!isNullOrEmpty(collection) && comparator != null) {
      Optional<T> optional = collection.stream().max(comparator);
      t = optional.isPresent() ? optional.get() : null;
    }
    return t;
  }

  /**
   * Find all list.
   *
   * @param <T>       the type parameter
   * @param list      the list
   * @param predicate the predicate
   * @return the list
   */
  public static <T> List<T> findAll(List<T> list, Predicate<? super T> predicate) {
    List<T> result = null;
    if (!isNullOrEmpty(list) && predicate != null) {
      result = list.stream().filter(predicate).collect(Collectors.toList());
    }
    return result;
  }

  /**
   * Insert list.
   *
   * @param <T>   the type parameter
   * @param list  the list
   * @param index the index
   * @param t     the t
   * @return the list
   */
  public static <T> List<T> insert(List<T> list, int index, T t) {
    List<T> result = null;
    if (!isNullOrEmpty(list) && t != null) {
      List<T> first = list.subList(0, index);
      List<T> last = list.subList(index, list.size());
      try {
        result = list.getClass().newInstance();
      } catch (Exception e) {
        result = new ArrayList<>();
      }
      result.addAll(first);
      result.add(t);
      result.addAll(last);
    }
    return result;
  }

  /**
   * Unique push boolean.
   *
   * @param <T>   the type parameter
   * @param item  the item
   * @param items the items
   * @return the boolean
   */
  public static <T> boolean uniquePush(T item, List<T> items) {
    boolean result = false;
    if (null != items && null != item && !items.contains(item)) {
      result = items.add(item);
    }
    return result;
  }

  /**
   * Intersect list.
   *
   * @param <T> the type parameter
   * @param a   the a
   * @param b   the b
   * @return the list
   */
  public static <T> List<T> intersect(List<T> a, List<T> b) {
    List<T> newList = new ArrayList<>();
    if (!isNullOrEmpty(a) && !isNullOrEmpty(b)) {
      for (T t : a) {
        if (b.contains(t)) {
          newList.add(t);
        }
      }
    }
    return newList;
  }
}
