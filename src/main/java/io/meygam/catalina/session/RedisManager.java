package io.meygam.catalina.session;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.session.PersistentManagerBase;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by saravana on 3/12/15.
 */
public class RedisManager extends PersistentManagerBase {
    private static final String name = "RedisManager";

    private final RedisStore redisStore;
    private String redisNodes;

    public RedisManager() {
        redisStore = new RedisStore();
        setStore(redisStore);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void initInternal() throws LifecycleException {
        this.setDistributable(true);
//        redisStore.setJedisCluster(getJedisCluster(getRedisNodes()));
        redisStore.setJedis(new Jedis(getRedisNodes()));
    }

    public String getRedisNodes() {
        return redisNodes;
    }

    public void setRedisNodes(String redisNodes) {
        this.redisNodes = redisNodes;
    }

    private JedisCluster getJedisCluster(String redisNodes) {
        String[] items = redisNodes.split(",");
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        for (int i = 0; i < items.length; i++) {
            jedisClusterNodes.add(new HostAndPort(items[i], 6379));
        }
        return new JedisCluster(jedisClusterNodes);
    }
}

