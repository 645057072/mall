package com.hmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {
    private static Logger logger= LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX="token_";

    private static LoadingCache<String, String> loadCache= CacheBuilder.newBuilder().initialCapacity(1000)
            .maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
                @Override
//                默认的数据加载实现，当调用get取值的时候，如果KEY没有对应的值，就调用这个方法进行加载
                public String load(String key) throws Exception {
                    return "null";
                }
            });
    public static void setKey(String key,String value){
        loadCache.put(key,value);
    }
    public static String getKey(String key){
        String value=null;
        try{
            value=loadCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        } catch (Exception e) {
           logger.error("laodCache logger error",e);
        }
        return null;
    }
}
