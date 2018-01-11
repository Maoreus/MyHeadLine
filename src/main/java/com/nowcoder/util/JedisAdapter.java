package com.nowcoder.util;

import com.alibaba.fastjson.JSON;

import com.nowcoder.controller.IndexController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import redis.clients.jedis.*;

import java.util.List;

/**
 *
 */
@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;


    /**
     * 利用redis的集合实现赞和踩的功能
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);
    }

    private Jedis getJedis() {
        //return jedis;
        return pool.getResource();
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return getJedis().get(key);
        } catch (Exception e) {
            LOGGER.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            LOGGER.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    /**
     * redis集合的sadd操作
     * @param key
     * @param value
     * @return
     */
    public long sadd(String key, String value) {
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }
        catch (Exception e){
            LOGGER.error("发生异常{}", e);
            return 0;
        }
        finally {
            if (jedis != null){
                //需要关闭已经使用的进程，否则会卡住
                jedis.close();
            }
        }
    }

    /**
     * redis集合的srem操作
     * @param key
     * @param value
     * @return
     */
    public long srem(String key, String value) {
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key, value);
        }
        catch (Exception e){
            LOGGER.error("发生异常{}", e);
            return 0;
        }
        finally {
            if (jedis != null){
                //需要关闭已经使用的进程，否则会卡住
                jedis.close();
            }
        }
    }

    /**
     * redis集合的sismember操作
     * @param key
     * @param value
     * @return
     */
    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        }
        catch (Exception e){
            LOGGER.error("发生异常{}", e);
            return false;
        }
        finally {
            if (jedis != null){
                //需要关闭已经使用的进程，否则会卡住
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }
        catch (Exception e){
            LOGGER.error("发生异常{}", e);
            return 0;
        }
        finally {
            if (jedis != null){
                //需要关闭已经使用的进程，否则会卡住
                jedis.close();
            }
        }
    }

    public void setex(String key, String value) {
        // 验证码, 防机器注册，记录上次注册时间，有效期3天
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.setex(key, 10, value);
        } catch (Exception e) {
            LOGGER.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            LOGGER.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            LOGGER.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setObject(String key, Object obj) {
        set(key, JSON.toJSONString(obj));
    }

    public <T> T getObject(String key, Class<T> clazz){
        //取出json
        String value = get(key);
        if (value != null){
            //重新解析json串
            return JSON.parseObject(value, clazz);
        }
        return null;
    }

}
