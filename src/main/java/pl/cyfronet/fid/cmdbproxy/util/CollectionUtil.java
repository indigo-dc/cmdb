package pl.cyfronet.fid.cmdbproxy.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CollectionUtil {

    public static <T> List<T> notNullable(List<T> list) {
        return Optional.ofNullable(list).orElse(Collections.<T> emptyList());
    }

    public static <K, V> Map<K, V> notNullable(Map<K, V> map) {
        return Optional.ofNullable(map).orElse(Collections.<K, V> emptyMap());
    }
}
