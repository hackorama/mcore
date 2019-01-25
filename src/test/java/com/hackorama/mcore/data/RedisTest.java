package com.hackorama.mcore.data;

import redis.clients.jedis.Jedis;

/**
 * Basic Redis tests, requires a Redis server on localhost.
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class RedisTest {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        System.out.println("PING : " + jedis.ping());
        jedis.close();
    }

}
