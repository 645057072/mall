package com.hmall.common;

import com.hmall.unit.PropertieUitl;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisShardedPool {
    private static ShardedJedisPool pool;//jedis连接池
    private static Integer maxTotal= Integer.parseInt(PropertieUitl.getProperty("redis.max.total","20")); //最大连接池
    private static Integer maxIdle=Integer.parseInt(PropertieUitl.getProperty("redis.max.idle","10"));//最小连接池
    private static Integer minIdle=Integer.parseInt(PropertieUitl.getProperty("redis.min.idle","2"));//最小连接池

    private static Boolean testOnBorrow=Boolean.parseBoolean(PropertieUitl.getProperty("redis.test.borrow","true"));//在borrow一个jedis实例时，是否需要进行验证.如果为TRUE,则可以使用该JEDIS的实例
    private static Boolean testOnReturn=Boolean.parseBoolean(PropertieUitl.getProperty("redis.test.return","false"));//在Return一个jedis实例时，是否需要进行验证.如果为TRUE,则可以使用该JEDIS的实例

    private static String redis1Ip=PropertieUitl.getProperty("redis1.ip");
    private static Integer redis1Port=Integer.parseInt(PropertieUitl.getProperty("redis1.prot"));

    private static String redis2Ip=PropertieUitl.getProperty("redis2.ip");
    private static Integer redis2Port=Integer.parseInt(PropertieUitl.getProperty("redis2.prot"));


    private static void initPool(){
        JedisPoolConfig poolConfig=new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);

        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);

        JedisShardInfo info1=new JedisShardInfo(redis1Ip,redis1Port,1000*2);
        JedisShardInfo info2=new JedisShardInfo(redis2Ip,redis2Port,1000*2);

        List<JedisShardInfo> jedisShardInfoList=new ArrayList<>(2);
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool=new ShardedJedisPool(poolConfig,jedisShardInfoList,Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    static {
        initPool();
    }

    public static ShardedJedis getResource(){
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis ){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args){
        ShardedJedis jedis=pool.getResource();
        for (int i=0;i<20;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);

        pool.destroy();
        System.out.print("program to end");
    }
}
