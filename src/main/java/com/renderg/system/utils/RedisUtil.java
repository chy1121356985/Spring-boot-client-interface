package com.renderg.system.utils;


import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 将参数中的字符串值设置为键的值，不设置过期时间
     * @param key
     * @param value 必须要实现 Serializable 接口
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }


    /**
     * 将参数中的字符串值设置为键的值，设置过期时间
     * @param key
     * @param value 必须要实现 Serializable 接口
     * @param timeout  过期秒数
     */
    public void set(String key, Object value, Long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取与指定键相关的值
     * @param key
     * @return
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置某个键的过期时间
     * @param key 键值
     * @param ttl 过期秒数
     */
    public boolean expire(String key, Long ttl) {
        return redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    /**
     * 判断某个键是否存在
     * @param key 键值
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 向集合添加元素
     * @param key
     * @param value
     * @return 返回值为设置成功的value数
     */
    public Long sAdd(String key, String... value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 获取集合中的某个元素
     * @param key
     * @return 返回值为redis中键值为key的value的Set集合
     */
    public  Set<Object> sGetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 将给定分数的指定成员添加到键中存储的排序集合中
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 返回指定排序集中给定成员的分数
     * @param key
     * @param value
     * @return
     */
    public Double zScore(String key, String value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    public Long setList(String key, String value){
        return redisTemplate.opsForList().leftPush(key,value);
    }

    public List<?> getList(String key){
        return redisTemplate.opsForList().range(key,0,-1);
    }

    /**
     * 删除指定的键
     * @param key
     * @return
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除多个键
     * @param keys
     * @return
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 模糊删除key
     * @param suffix
     */
    public  void  deleteBySuffix(String suffix ) {
        //String suffix = "R20180903001735213045";
        Set<String> keys = redisTemplate.keys(suffix+"*");
        for (String str : keys) {
            System.out.print("key的值"+str + " ");
        }
        redisTemplate.delete(keys);
    };

    public  List<Object>  selectBySuffix(String suffix ) {
        //String suffix = "R20180903001735213045";
        List<Object> list  = new ArrayList<Object>();
        Set<String> keys = redisTemplate.keys(suffix+"*");
        for (String str : keys) {
            //System.out.print("key的值"+str + " ");
            list.add(get(str));
        }
        return list;
    };

    /**
     * 搜索模糊key
     * @param suffix
     * @return
     */
    public  List<String>  selectBySuffixToKey(String suffix ) {
        List<String> list  = new ArrayList<String>();
        Set<String> keys = redisTemplate.keys(suffix+"*");
        for (String str : keys) {
            list.add(str);
        }
        return list;
    };

    /**
     * 添加坐标
     * **/
    public void cacheGeo(String key, Double x, Double y, String member) {
        GeoOperations<String, Object> geoOps = redisTemplate.opsForGeo();
        geoOps.add(key, new Point(x, y) , member);
    }

    /**
     * 删除坐标
     * **/
    public void removeGeo(String key, String...members) {
        GeoOperations<String, Object> geoOps = redisTemplate.opsForGeo();
        geoOps.remove(key, members);
    }

    /**
     * 比较2个坐标距离
     * **/
    public Distance distanceGeo(String key, String member1, String member2) {
        GeoOperations<String, Object> geoOps = redisTemplate.opsForGeo();
        return geoOps.distance(key, member1, member2);
    }

    /**
     * 获取坐标
     * **/
    public List<Point> positionGeo(String key, String... members){
        GeoOperations<String, Object> geoOps = redisTemplate.opsForGeo();
        return geoOps.position(key, members);
    }

}