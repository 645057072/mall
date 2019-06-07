package com.hmall.unit;

import com.hmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedPoolUtil {
    /**
     * 使用key给redis设置过期时间。expire默认时间为秒
     * @param key
     * @param expire
     * @return
     */

    public static Long expire(String key,int expire){
        ShardedJedis jedis=null;
        Long result=null;
        try {
            jedis= RedisShardedPool.getResource();
            result=jedis.expire(key,expire);
        }catch (Exception e){
            log.error("expire key:{} expire:{}",key,expire,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    /**
     * 使用setex方法给redis设置一个有效时间
     * @param key
     * @param value
     * @param exTime
     * @return
     */
    //exTime的默认时间为秒
    public static String setex(String key,String value,int exTime){
        ShardedJedis jedis=null;
        String result=null;

        try {
            jedis=RedisShardedPool.getResource();
            result=jedis.setex(key,exTime,value);
        }catch (Exception e){
            log.error("set key:{} exTime:{} value:{} error",key,exTime,value,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;

    }



    /**
     *使用set方法设置redis中set信息
     * @param key
     * @param value
     * @return
     */

    public static String set(String key,String value){
        ShardedJedis jedis=null;
        String result=null;

        try {
            jedis=RedisShardedPool.getResource();
            result=jedis.set(key,value);
        }catch (Exception e){
            log.error("set key:{} value:{} error",key,value,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;

    }

    /**
     * 通过获取key值，获取Redis中的value
     * @param key
     * @return
     */
    public static String get(String key){
        ShardedJedis jedis=null;
        String result=null;

        try {
            jedis=RedisShardedPool.getResource();
            result=jedis.get(key);
        }catch (Exception e){
            log.error("set key:{} error",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;

    }

    /**
     * 通过key值移除redis信息
     * @param key
     * @return
     */

    public static Long del(String key){
        ShardedJedis jedis=null;
        Long result=null;
        try {
            jedis=RedisShardedPool.getResource();
            result=jedis.del(key);
        }catch (Exception e){
            log.error("del key:{}",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    public static void main(String[] args) {
        ShardedJedis jedis=RedisShardedPool.getResource();
        jedis.set("Leoest","leoset");
        jedis.setex("Leosetex",60*10,"Leosetex");
        jedis.expire("Leoest",60*2);
        jedis.del("Leosetex");
        System.out.println("Program to end");
    }

}
