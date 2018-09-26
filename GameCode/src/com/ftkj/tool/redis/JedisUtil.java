package com.ftkj.tool.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jredis.ri.alphazero.support.DefaultCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.util.SafeEncoder;

/**
 * @author tim.huang
 * 2017年5月15日
 */
public class JedisUtil {
    private static final Logger log = LoggerFactory.getLogger(JedisUtil.class);
    /**
     * 缓存生存时间 </BR>
     * 默认1天, 秒数
     */
    private final int expire = 86400;
    // /** 操作Key的方法 */
    // public Keys KEYS;
    // /** 对存储结构为String类型的操作 */
    // public Strings STRINGS;
    // /** 对存储结构为List类型的操作 */
    // public Lists LISTS;
    // /** 对存储结构为Set类型的操作 */
    // public Sets SETS;
    // /** 对存储结构为HashMap类型的操作 */
    // public Hash HASH;
    // /** 对存储结构为Set(排序的)类型的操作 */
    // public SortSet SORTSET;
    private JedisPool jedisPool = null;
    private ShardedJedisPool shardedJedisPool = null;

    public JedisUtil(Jredis j) {
        init(j);
    }

    /**
     * 构建redis连接池
     */
    private void init(Jredis j) {
        // ResourceBundle bundle = ResourceBundle.getBundle("redis");
        // if (bundle == null) {
        // throw new IllegalArgumentException(
        // "[redis.properties] is not found!");
        // }
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(j.getConnectionCount());
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            config.setMaxWaitMillis(5_000);// 10 sec
            jedisPool = new JedisPool(config, j.getHost(), j.getPort(), RedisConfig.TIMEOUT, j.getPassword(), j.getDatabase());
            log.info("redis init. host {}:{} db {}", j.getHost(), j.getPort(), j.getDatabase());
        }
        // KEYS = new Keys();
        // STRINGS = new Strings();
        // LISTS = new Lists();
        // SETS = new Sets();
        // HASH = new Hash();
        // SORTSET = new SortSet();
    }

    public static void main(String[] args) {
        // Jredis jc = new Jredis();
        // jc.setPort(6379);
        // jc.setConnectionCount(2);
        // jc.setDatabase(0);
        // jc.setHost("192.168.10.181");
        // jc.setPassword("zgame2017");
        // JedisUtil util = new JedisUtil(jc);

        JedisPoolConfig configJedis = new JedisPoolConfig();
        configJedis.setMaxTotal(2);
        configJedis.setMaxWaitMillis(5_000);

        final JedisPool pool = new JedisPool(configJedis, "localhost");

        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            try {
                Jedis j = pool.getResource();
                System.out.println("get client " + i + " client " + j);
                if (i % 3 == 0) {
                    j.close();
                    System.out.println("close client " + i);
                }
            } catch (Exception e) {
                System.out.println(System.currentTimeMillis() - start);
                System.out.println("Timeout waiting for resource: " + e.getMessage());
            }
        }

    }

    public JedisPool getPool() {
        return jedisPool;
    }

    public ShardedJedisPool getShardedJedisPool() {
        return shardedJedisPool;
    }

    /**
     * 从jedis连接池中获取获取jedis对象
     *
     * @return
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 回收jedis
     *
     * @param jedis
     */
    public void returnJedis(Jedis jedis) {
        jedis.close();
        // jedisPool.returnResource(jedis);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param seconds
     * @author ruan 2013-4-11
     */
    public void expire(String key, int seconds) {
        if (seconds <= 0) {
            return;
        }
        Jedis jedis = getJedis();
        jedis.expire(key, seconds);
        returnJedis(jedis);
    }

    /**
     * 设置默认过期时间
     *
     * @param key
     * @author ruan 2013-4-11
     */
    public void expire(String key) {
        expire(key, expire);
    }

    /**
     * 删除键,不可以带带匹配字符
     *
     * @param key
     */
    public void del(String key) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            jedis.del(keybyte);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    // 对List操作的命令
    // public <T extends Serializable> void pushxx(String key, List<T> obj, int expire) {
    // try (Jedis jedis = getJedis()) {
    // byte[] keybyte = DefaultCodec.encode(key);
    // jedis.set(keybyte, DefaultCodec.encode(new ListObj<T>(obj)));
    // jedis.expire(keybyte, expire);
    // } catch (Exception e) {
    // log.error("Redis Error->key[{}],stack[{}]", key, e);
    // }
    // }

    // 对List操作的命令
    // public <T extends Serializable> void pushxx(String key, List<T> obj) {
    // try (Jedis jedis = getJedis()) {
    // byte[] keybyte = DefaultCodec.encode(key);
    // jedis.set(keybyte, DefaultCodec.encode(new ListObj<T>(obj)));
    // jedis.expire(keybyte, expire);
    // } catch (Exception e) {
    // log.error("Redis Error->key[{}],stack[{}]", key, e);
    // }
    // }

    public <T extends Serializable> void set(String key, T obj, int expireSeconds) {
        // FIXME obj 非序列化的时候，直接跑出异常
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            jedis.set(keybyte, DefaultCodec.encode(obj));
            jedis.expire(keybyte, expireSeconds);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    /**
     * 不支持基础类型，请传字符串
     *
     * @param key
     * @param obj
     */
    public <T extends Serializable> void set(String key, T obj) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            // if(obj instanceof Integer){
            // int val = (int) obj;
            // jedis.set(keybyte, DefaultCodec.encode(""+val));
            // return;
            // }

            jedis.set(keybyte, DefaultCodec.encode(obj));
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    // 取数据
    public <T extends Serializable> List<T> listxx(String key) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            byte[] databyte = jedis.get(keybyte);
            if (databyte == null) {
                return null;
            }
            ListObj<T> res = DefaultCodec.decode(databyte);
            return res.getList();
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    // 返回数据库中名称为key的string的value
    public String getStr(String key) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            byte[] databyte = jedis.get(keybyte);
            if (databyte == null) {
                return null;
            }
            String res = DefaultCodec.decode(databyte);
            return res;
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    /**
     * 考虑redis穿透问题
     * 返回数据库中名称为key的string的value
     *
     * @param key
     * @return 默认空返回 null
     */
    public Integer getInt(String key) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            byte[] databyte = jedis.get(keybyte);
            if (databyte == null) {
                return null;
            }
            String res = DefaultCodec.decode(databyte);
            // return res;
            return Ints.tryParse(res);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    /**
     * FIXME 考虑redis穿透问题
     * 如果空就返回0
     * 注意，不能存-1的值，-1当做0来处理
     *
     * @param key
     * @return
     */
    public int getIntNullIsZero(String key) {
        Integer value = getInt(key);
        return value == null ? 0 : value;
    }

    public <T extends Serializable> T getObj(String key) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            byte[] databyte = jedis.get(keybyte);
            if (databyte == null) {
                return null;
            }
            T res = DefaultCodec.decode(databyte);
            return res;
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    /**
     * @param key
     * @return
     */
    public long getLong(String key) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            byte[] databyte = jedis.get(keybyte);
            if (databyte == null) {
                return 0;
            }
            String res = DefaultCodec.decode(databyte);
            return Longs.tryParse(res);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return 0;
    }

    // 确认一个key是否存在
    public boolean exits(String key) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            return jedis.exists(keybyte);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return true;
    }

    public long incr(String key) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            return jedis.incr(keybyte);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return 0;
    }

    public long incr(String key, int timeout) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            long num = jedis.incr(keybyte);
            jedis.expire(keybyte, timeout);
            return num;
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return 0;
    }

    /**
     * 在尾部添加
     *
     * @param key
     * @param value
     */
    public <T extends Serializable> void addListValueR(String key, T value) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            jedis.rpush(keybyte, DefaultCodec.encode(value));
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    /**
     * 在头部添加
     *
     * @param key
     * @param value
     */
    public <T extends Serializable> void addListValueL(String key, T value) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            jedis.lpush(keybyte, DefaultCodec.encode(value));
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    /**
     * 在头部添加
     *
     * @param key
     * @param value
     */
    public <T extends Serializable> void addListValueLExp(String key, T value) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            jedis.lpush(keybyte, DefaultCodec.encode(value));
            jedis.expire(keybyte, expire);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    // 取数据
    public <T extends Serializable> List<T> getList(String key) {
        return getList(key, 0, -1);
    }

    // 取数据
    public <T extends Serializable> List<T> getList(String key, int start, int end) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            List<byte[]> databyte = jedis.lrange(keybyte, start, end);
            if (databyte == null) {
                return Lists.newArrayList();
            }
            return DefaultCodec.decode(databyte);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return Lists.newArrayList();
    }

    public <T extends Serializable> long removeListValue(String key, T value) {
        if (value == null) {
            return 0;
        }
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            byte[] valuebyte = DefaultCodec.encode(value);
            long count = jedis.lrem(keybyte, 0, valuebyte);
            // List<byte[]> databyte = jedis.lrange(keybyte, 0, -1);
            // if(databyte == null) return null;
            return count;
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return 0;
    }

    public <T extends Serializable> void rpush(String key, List<T> obj) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            jedis.del(keybyte);
            for (T t : obj) {
                jedis.rpush(keybyte, DefaultCodec.encode(t));
            }
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    public <T extends Serializable> void rpush(String key, List<T> list, int expire) {
        try (Jedis jedis = getJedis()) {
            jedis.del(key);
            byte[][] values = new byte[list.size()][];
            for (int i = 0; i < list.size(); i++) {
                values[i] = DefaultCodec.encode(list.get(i));
            }
            jedis.rpush(DefaultCodec.encode(key), values);
            jedis.expire(key, expire);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    public Set<String> smembers(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.smembers(key);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }
    
    public Long sadd(final String key, final String... members) {
        try (Jedis jedis = getJedis()) {
            Long ret = jedis.sadd(key, members);
            return ret;
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return -1L;
    }

    public Long sadd(final String key, int expire, final String... members) {
        try (Jedis jedis = getJedis()) {
            Long ret = jedis.sadd(key, members);
            jedis.expire(key, expire);
            return ret;
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return -1L;
    }

    public <T extends Serializable, K extends Serializable> T hget(String key, K field) {
        try (Jedis jedis = getJedis()) {
            byte[] source = jedis.hget(DefaultCodec.encode(key), DefaultCodec.encode(field));
            if (source == null) {
                return null;
            }
            return DefaultCodec.decode(source);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    public <T extends Serializable> T hget(byte[] key, byte[] field) {
        try (Jedis jedis = getJedis()) {
            byte[] source = jedis.hget(key, field);
            if (source == null) {
                return null;
            }
            return DefaultCodec.decode(source);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    public <T extends Serializable, K extends Serializable> void putMapValue(String key, K field, T value) {
        try (Jedis jedis = getJedis()) {
            jedis.hset(DefaultCodec.encode(key), DefaultCodec.encode(field), DefaultCodec.encode(value));
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    /**
     * 没存活时间
     *
     * @param key
     * @param field
     * @param value
     */
    public void hset(String key, String field, String value) {
        try (Jedis jedis = getJedis()) {
            jedis.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), SafeEncoder.encode(value));
        } catch (Exception e) {
            log.error("hset Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    public void hsetB(String key, String field, byte[] value) {
        try (Jedis jedis = getJedis()) {
            jedis.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value);
        } catch (Exception e) {
            log.error("hset Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    public Map<Long, byte[]> hgetallB(String key) {
        try (Jedis jedis = getJedis()) {
            Map<byte[], byte[]> hash = jedis.hgetAll(SafeEncoder.encode(key));
            Map<Long, byte[]> map = Maps.newHashMap();
            if (hash == null || hash.isEmpty()) {
                return map;
            }
            hash.forEach((k, v) -> map.put(Long.valueOf(SafeEncoder.encode(k)), v));
            return map;
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    /**
     * @param key
     * @param field
     */
    public void hdel(String key, String field) {
        try (Jedis jedis = getJedis()) {
            jedis.hdel(SafeEncoder.encode(key), SafeEncoder.encode(field));
        } catch (Exception e) {
            log.error("hdel Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    /**
     * 有存活时间的
     *
     * @param key
     * @param field
     * @param value
     */
    public <T extends Serializable, K extends Serializable> void putMapValueExp(String key, K field, T value) {
        try (Jedis jedis = getJedis()) {
            byte[] keybyte = DefaultCodec.encode(key);
            jedis.hset(keybyte, DefaultCodec.encode(field), DefaultCodec.encode(value));
            jedis.expire(keybyte, expire);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    public <T extends Serializable, K extends Serializable> void removeMapValue(String key, K field) {
        try (Jedis jedis = getJedis()) {
            jedis.hdel(DefaultCodec.encode(key), DefaultCodec.encode(field));
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    public byte[] getMapBytes(String key, String field) {
        try (Jedis jedis = getJedis()) {
            return jedis.hget(DefaultCodec.encode(key), DefaultCodec.encode(field));
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    public <T extends Serializable> List<T> getMapValues(String key) {
        try (Jedis jedis = getJedis()) {
            return DefaultCodec.decode(jedis.hvals(DefaultCodec.encode(key)));
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    public <T extends Serializable, K extends Serializable> Map<T, K> getMapAllKeyValues(String key) {
        try (Jedis jedis = getJedis()) {
            Map<byte[], byte[]> mapData = jedis.hgetAll(DefaultCodec.encode(key));
            List<T> keys = DefaultCodec.decode(Lists.newArrayList(mapData.keySet()));
            Map<T, K> result = Maps.toMap(keys, k -> DefaultCodec.decode(mapData.get(DefaultCodec.encode(k))));
            return Maps.newHashMap(result);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    public Map<Integer, Integer> hgetallIKIV(String key) {
        return hgetall(key, Integer::parseInt, Integer::parseInt, HashMap::new);
    }
    
    public Map<Integer, Integer> hgetallIKIV2(String key) {
        return hgetall2(key, Integer::parseInt, Integer::parseInt, HashMap::new);
    }

    public Map<Integer, String> hgetallIKSV(String key) {
        return hgetall(key, Integer::parseInt, String::toString, HashMap::new);
    }

    public Map<Long, String> hgetallLKSV(String key) {
        return hgetall(key, Long::parseLong, String::toString, HashMap::new);
    }

    public <K, V, M extends Map<K, V>> M hgetall(String key, Function<String, ? extends K> keyMapper, Function<String, ? extends V> valueMapper, Supplier<M> mapSupplier) {
        try (Jedis jedis = getJedis()) {
            Map<byte[], byte[]> hash = jedis.hgetAll(SafeEncoder.encode(key));
            M map = mapSupplier.get();
            if (hash == null || hash.isEmpty()) {
                return map;
            }
            hash.forEach((k, v) -> map.put(keyMapper.apply(SafeEncoder.encode(k)), valueMapper.apply(SafeEncoder.encode(v))));
            return map;
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }
    
    /**
     * 上面方法hgetall,有问题,重新写一个.
     * @param key
     * @param keyMapper
     * @param valueMapper
     * @param mapSupplier
     * @return
     */
    public <K, V, M extends Map<K, V>> M hgetall2(String key, Function<String, ? extends K> keyMapper, Function<String, ? extends V> valueMapper, Supplier<M> mapSupplier) {
        try (Jedis jedis = getJedis()) {
            Map<byte[], byte[]> hash = jedis.hgetAll(SafeEncoder.encode(key));
            M map = mapSupplier.get();
            if (hash == null || hash.isEmpty()) {
                return map;
            }
            hash.forEach((k, v) -> map.put(DefaultCodec.decode(k), DefaultCodec.decode(v)));
            return map;
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return null;
    }

    public <T extends Serializable, K extends Serializable> void hmset(String key, Map<T, K> map) {
        if (map == null || map.isEmpty()) {
            del(key);
            return;
        }
        try (Jedis jedis = getJedis()) {
            Map<byte[], byte[]> hash = new HashMap<>(map.size());
            map.forEach((k, v) -> hash.put(DefaultCodec.encode(k), DefaultCodec.encode(v)));
            jedis.hmset(DefaultCodec.encode(key), hash);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    public <T extends Serializable> long zrevrank(String key, T value) {
        try (Jedis jedis = getJedis()) {
            byte[] keys = DefaultCodec.encode(key);
            byte[] values = DefaultCodec.encode(value);
            Long rank = jedis.zrevrank(keys, values);
            return (int) (rank == null ? -1 : rank);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return -1;
    }

    public <T extends Serializable> long getSetScore(String key, T value) {
        try (Jedis jedis = getJedis()) {
            byte[] keys = DefaultCodec.encode(key);
            byte[] values = DefaultCodec.encode(value);
            Double rank = jedis.zscore(keys, values);
            return (int) (rank == null ? -1 : rank);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
        return -1;
    }

    public <T extends Serializable> void zadd(String key, double score, T member) {

        try (Jedis jedis = getJedis()) {
            byte[] keys = DefaultCodec.encode(key);
            byte[] values = DefaultCodec.encode(member);
            jedis.zadd(keys, score, values);
        } catch (Exception e) {
            log.error("Redis Error->key[{}],stack[{}]", key, e);
        }
    }

    /**
     * 删除键,可以带带匹配字符
     * 删除匹配的key<br>
     * 如以my为前缀的则 参数为"my*"
     *
     * @param key
     */
    public void delRedisCache(String key) {
        try (Jedis j = getJedis()) {
            Set<String> keys = j.keys(key);
            if (keys == null || keys.size() <= 0) {
                return;
            }
            keys.forEach(k -> j.del(DefaultCodec.encode(k)));
            log.info("总共清理了{}个{}相关的缓存数据", keys.size(), key);
        }
    }

    public <R> R execute(Function<Jedis, R> command) {
        try (Jedis jedis = getJedis()) {
            R r = command.apply(jedis);
            return r;
        } catch (Exception e) {
            log.error("xrmatch redis. " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * @see Jedis#zrem(java.lang.String, java.lang.String...)
     */
    public Long zrem(final String key, final String... members) {
        try (Jedis jedis = getJedis()) {
            Long r = jedis.zrem(key, members);
            if (log.isDebugEnabled()) {
                log.debug("redis zrem. key {} members {} ret {}", key, Arrays.toString(members), r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zrem. " + e.getMessage(), e);
        }
        return -1L;
    }

    /**
     * @see Jedis#zadd(String, Map)
     */
    public Long zadd(final String key, final Map<String, Double> scoreMembers) {
        if (scoreMembers == null || scoreMembers.isEmpty()) {
            return 0L;
        }
        try (Jedis jedis = getJedis()) {
            Long r = jedis.zadd(key, scoreMembers);
            if (log.isDebugEnabled()) {
                log.debug("redis zadd. key {} scoreMembers {} ret {}", key, scoreMembers, r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zadd. " + e.getMessage(), e);
        }
        return -1L;
    }

    /**
     * @see Jedis#zincrby(String, double, String)
     */
    public Double zincrby(final String key, final double score, final String member) {
        try (Jedis jedis = getJedis()) {
            Double r = jedis.zincrby(key, score, member);
            if (log.isDebugEnabled()) {
                log.debug("redis zincrby. key {} score {} members {} ret {}", key, score, member, r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zincrby. " + e.getMessage(), e);
        }
        return 0d;
    }

    /**
     * @see Jedis#zadd(String, double, String)
     */
    public Long zadd(final String key, final double score, final String member) {
        try (Jedis jedis = getJedis()) {
            Long r = jedis.zadd(key, score, member);
            if (log.isDebugEnabled()) {
                log.debug("redis zadd. key {} score {} members {} ret {}", key, score, member, r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zadd. " + e.getMessage(), e);
        }
        return -1L;
    }

    /**
     * Return the sorted set cardinality (number of elements). If the key does not exist 0 is
     * returned, like for empty sorted sets.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @return the cardinality (number of elements) of the set as an integer.
     */
    public Long zcard(final String key) {
        try (Jedis jedis = getJedis()) {
            Long r = jedis.zcard(key);
            if (log.isDebugEnabled()) {
                log.debug("redis zcard. key {} ret {}", key, r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zcard. " + e.getMessage(), e);
        }
        return 0L;
    }

    public Long zrevrank(final String key, String member) {
        try (Jedis jedis = getJedis()) {
            Long r = jedis.zrevrank(key, member);
            if (log.isDebugEnabled()) {
                log.debug("redis zrevrank. key {} member {} ret {}", key, member, r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zrevrank. " + e.getMessage(), e);
        }
        return null;
    }

    public Double zscore(final String key, String member) {
        try (Jedis jedis = getJedis()) {
            Double r = jedis.zscore(key, member);
            if (log.isDebugEnabled()) {
                log.debug("redis zscore. key {} member {} ret {}", key, member, r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zscore. " + e.getMessage(), e);
        }
        return null;
    }

    public Long zrank(final String key, String member) {
        try (Jedis jedis = getJedis()) {
            Long r = jedis.zrank(key, member);
            if (log.isDebugEnabled()) {
                log.debug("redis zrank. key {} member {} ret {}", key, member, r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zrank. " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see Jedis##zunionstore(String, String...)
     */
    public Long zunionstore(final String dstkey, final String... sets) {
        try (Jedis jedis = getJedis()) {
            Long r = jedis.zunionstore(dstkey, sets);
            if (log.isDebugEnabled()) {
                log.debug("redis zunionstore. dstkey {} sets {} ret {}", dstkey, Arrays.toString(sets), r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zunionstore. " + e.getMessage(), e);
        }
        return -1L;
    }

    public List<String> zrevrange(final String key, final long start, final long end) {
        try (Jedis jedis = getJedis()) {
            List<String> r = zrevrange0(jedis, key, start, end);
            if (log.isDebugEnabled()) {
                log.debug("redis zrevrange. key {} start {} end {} ret size {}", key, start, end, r.size());
            }
            return r;
        } catch (Exception e) {
            log.error("redis zrevrange. " + e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public List<String> zrevrange0(Jedis jedis, final String key, final long start, final long end) {
        checkIsInMultiOrPipeline(jedis);
        jedis.getClient().zrevrange(key, start, end);
        final List<String> members = jedis.getClient().getMultiBulkReply();
        if (members == null) {
            return Collections.emptyList();
        }
        return members;
    }

    public List<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        try (Jedis jedis = getJedis()) {
            List<Tuple> r = zrevrangeWithScores0(jedis, key, start, end);
            if (log.isDebugEnabled()) {
                log.debug("redis zrevrangeWithScores. key {} start {} end {} ret size {}", key, start, end, r.size());
            }
            return r;
        } catch (Exception e) {
            log.error("redis zrevrangeWithScores. " + e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private List<Tuple> zrevrangeWithScores0(Jedis jedis, String key, long start, long end) {
        checkIsInMultiOrPipeline(jedis);
        jedis.getClient().zrevrangeWithScores(key, start, end);
        return getTupledSet(jedis);
    }

    private List<Tuple> getTupledSet(Jedis jedis) {
        checkIsInMultiOrPipeline(jedis);
        List<String> membersWithScores = jedis.getClient().getMultiBulkReply();
        if (membersWithScores == null) {
            return Collections.emptyList();
        }
        if (membersWithScores.isEmpty()) {
            return Collections.emptyList();
        }
        List<Tuple> set = new ArrayList<>(membersWithScores.size() / 2);
        Iterator<String> iterator = membersWithScores.iterator();
        while (iterator.hasNext()) {
            set.add(new Tuple(iterator.next(), Double.parseDouble(iterator.next())));
        }
        return set;
    }

    public static final class Tuple implements Serializable {
        private static final long serialVersionUID = 1316846754146129876L;
        private final String element;
        private final double score;

        public Tuple(String element, double score) {
            this.element = element;
            this.score = score;
        }

        public String getElement() {
            return element;
        }

        public double getScore() {
            return score;
        }

    }

    protected void checkIsInMultiOrPipeline(Jedis jedis) {
        if (jedis.getClient().isInMulti()) {
            throw new JedisDataException("Cannot use Jedis when in Multi. Please use Transation or reset jedis state.");
        }
    }

    /**
     * Remove all elements in the sorted set at key with rank between start and end. Start and end are
     * 0-based with rank 0 being the element with the lowest score. Both start and end can be negative
     * numbers, where they indicate offsets starting at the element with the highest rank. For
     * example: -1 is the element with the highest score, -2 the element with the second highest score
     * and so forth.
     *
     * <b>Time complexity:</b> O(log(N))+O(M) with N being the number of elements in the sorted set
     * and M the number of elements removed by the operation
     */
    public Long zremrangeByRank(final String key, final long start, final long end) {
        try (Jedis jedis = getJedis()) {
            Long r = jedis.zremrangeByRank(key, start, end);
            if (log.isDebugEnabled()) {
                log.debug("redis zremrangeByRank. key {} start {} end {} ret {}", key, start, end, r);
            }
            return r;
        } catch (Exception e) {
            log.error("redis zremrangeByRank. " + e.getMessage(), e);
        }
        return -1L;
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, final int count) {
        try (Jedis jedis = getJedis()) {
            Set<String> tuple = jedis.zrevrangeByScore(key, max, min, offset, count);
            if (log.isDebugEnabled()) {
                log.debug("redis zrevrangeByScore. key {} max {} min {} offset {} count {}", key, max, min, offset, count);
            }
            return tuple;
        } catch (Exception e) {
            log.error("redis zremrangeByRank. " + e.getMessage(), e);
        }
        return null;
    }

    public String str(Object obj) {
        return String.valueOf(obj);
    }

    public String str(char c) {
        return String.valueOf(c);
    }

    public String str(int i) {
        return Integer.toString(i);
    }

    public String str(long l) {
        return Long.toString(l);
    }

    public class RedisConfig {
        // 可用连接实例的最大数目，默认值为8；
        // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        public static final int MAX_ACTIVE = 1024;

        // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
        public static final int MAX_IDLE = 200;

        // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
        public static final int MAX_WAIT = 10000;

        public static final int TIMEOUT = 10000;

        public static final int RETRY_NUM = 5;
    }
}
