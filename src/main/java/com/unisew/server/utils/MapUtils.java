package com.unisew.server.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtils {

    public static Map<String, Object> build(List<String> keys, List<Object> values) {
        if (keys.size() != values.size()) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();

        for (int i = 0; i < keys.size(); i++) {
            result.put(keys.get(i), values.get(i));
        }

        return result;
    }
}
