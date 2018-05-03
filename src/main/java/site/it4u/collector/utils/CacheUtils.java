package site.it4u.collector.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * cache implementation via ConcurrentHashMap
 */
public class CacheUtils {

    private CacheUtils(){}

    private static ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();

    private static CacheUtils instance = new CacheUtils();

    public static CacheUtils getInstance() {
        return instance;
    }

    public void putIntoCache(String key, String value) {
        concurrentHashMap.put(key, value);
    }

    public String getDataFromCache(String key) {
        return concurrentHashMap.get(key);
    }

    public void clearCacheByKey(String key) {
        concurrentHashMap.remove(key);
    }

    public void clearCache() {
        concurrentHashMap.clear();
    }
}
