package com.hackorama.mcore.data;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import redis.clients.jedis.Jedis;

/**
 * Basic Redis tests, requires a Redis server on localhost.
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class RedisTest {

    public static void jedisTest() {
        Jedis jedis = new Jedis("localhost");
        System.out.println("PING : " + jedis.ping());
        jedis.close();
    }

    public static void main(String[] args) {
        jedisTest();
        redissonTest();
    }

    private static void redissonTest() {
        Config config = new Config();
        config.useSingleServer().setAddress("localhost:6379");
        RedissonClient client = Redisson.create(config);
        System.out.println("PING : " + client.getNodesGroup().pingAll());
        client.shutdown();
    }

}
