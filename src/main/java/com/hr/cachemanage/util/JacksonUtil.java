package com.hr.cachemanage.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by r.hu on 2020/7/10.
 */
public abstract class JacksonUtil {

    private static final Logger _logger = LoggerFactory.getLogger(JacksonUtil.class);

    private static final String TIMEFORMAT_STANDARD = "yyyy-MM-dd HH:mm:ss";

    private static final Map<Class<?>, Class<?>> CLASSES =
        Arrays.asList(
            String.class,
            Double.class,
            Long.class,
            Integer.class,
            Float.class,
            Short.class,
            Byte.class,
            Character.class,
            Boolean.class,
            double.class,
            long.class,
            int.class,
            short.class,
            byte.class,
            char.class,
            boolean.class)
            .stream()
            .collect(Collectors.toMap(key -> key, value -> value));

    private static ObjectMapper _reader;
    private static ObjectMapper _writer;

    static {
        _reader = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 设置键可不加双引号，以应对配置系统中大量json键中无双引号情况
        _reader.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        _reader.setDateFormat(new SimpleDateFormat(TIMEFORMAT_STANDARD));
        _writer = new ObjectMapper();
        _writer.setSerializationInclusion(Include.NON_NULL);
        _writer.setDateFormat(new SimpleDateFormat(TIMEFORMAT_STANDARD));
    }

    /**
     * To json string. 基础方式
     *
     * @param object the object
     * @return the string
     */
    public static String toJson(Object object) {

        try {
            return _writer.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            _logger.error("序列化报错", e);
            return null;
        }
    }

    /**
     * To json string. 支持传入需要忽略的字段
     *
     * @param object the object
     * @param ignoredNames the ignored names
     * @return the string
     */
    public static String toJson(Object object, List<String> ignoredNames) {
        if (object != null && CLASSES.containsKey(object.getClass())) {
            return object.toString();
        }
        JsonNode jsonNode = _writer.valueToTree(object);
        for (String ignoredName : ignoredNames) {
            ((ObjectNode) jsonNode).remove(ignoredName);
        }

        return jsonNode.toString();
    }

    /**
     * To json.去掉schema
     */
    public static String toJsonFilterSchema(Object object) {
        if (object != null && CLASSES.containsKey(object.getClass())) {
            return object.toString();
        }
        JsonNode jsonNode = _writer.valueToTree(object);
        Iterator<String> keys = jsonNode.fieldNames();
        while (keys.hasNext()) {
            String jsonKey = (String) keys.next();
            if (jsonKey.equalsIgnoreCase("SCHEMA")) {
                ((ObjectNode) jsonNode).remove(jsonKey);
            }
        }

        return jsonNode.toString();
    }


}
