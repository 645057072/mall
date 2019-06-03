package com.hmall.common;

import com.hmall.unit.PropertieUitl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pool;//jedis连接池
    private static Integer maxTotal= Integer.parseInt(PropertieUitl.getProperty("redis.max.total","20")); //最大连接池
    private static Integer maxIdle=Integer.parseInt(PropertieUitl.getProperty("redis.max.idle","10"));//最小连接池
    private static Integer minIdle=Integer.parseInt(PropertieUitl.getProperty("redis.min.idle","2"));//最小连接池

    private static Boolean testOnBorrow=Boolean.parseBoolean(PropertieUitl.getProperty("redis.test.borrow","true"));//在borrow一个jedis实例时，是否需要进行验证.如果为TRUE,则可以使用该JEDIS的实例
    private static Boolean testOnReturn=Boolean.parseBoolean(PropertieUitl.getProperty("redis.test.return","false"));//在Return一个jedis实例时，是否需要进行验证.如果为TRUE,则可以使用该JEDIS的实例

    private static String redisIp=PropertieUitl.getProperty("redis.ip");
    private static Integer redisPort=Integer.parseInt(PropertieUitl.getProperty("redis.prot"));

    private static void initPool(){
        JedisPoolConfig poolConfig=new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);

        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);

        pool=new JedisPool(poolConfig,redisIp, redisPort,1000*2);

    }

    static {
        initPool();
    }

    public static Jedis getResource(){
       return pool.getResource();
    }

    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis ){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args){
        Jedis jedis=pool.getResource();
        jedis.set("Leovi","Leovi");
       returnResource(jedis);

       pool.destroy();
       System.out.print("program to end");
    }
}
