package io.meygam.catalina.session;

import org.apache.catalina.Session;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StoreBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * Created by saravana on 3/16/15.
 */
public class RedisStore extends StoreBase {

    private static final Log log = LogFactory.getLog(RedisStore.class);

    private JedisCluster jedisCluster;
    private Set<String> keys = Collections.emptySet();

    @Override
    public int getSize() throws IOException {
        return getJedisCluster().dbSize().intValue();
    }

    @Override
    public String[] keys() throws IOException {
        return keys.toArray(new String[0]);
    }

    @Override
    public Session load(String id) throws ClassNotFoundException, IOException {
        StandardSession standardSession = (StandardSession) getManager().createSession(id);
        Map<String, String> sessionAttributes = getJedisCluster().hgetAll(id);
        for (Map.Entry<String, String> entry : sessionAttributes.entrySet()) {
            standardSession.setAttribute(entry.getKey(), entry.getValue());
        }
        keys.add(id);
        return standardSession;
    }

    @Override
    public void remove(String id) throws IOException {
        getJedisCluster().del(id);
    }

    @Override
    public void clear() throws IOException {
        getJedisCluster().flushAll();
        keys.clear();
    }

    @Override
    public void save(Session session) throws IOException {
        StandardSession standardSession = (StandardSession) session;
        for (Enumeration<String> attributeNames = standardSession.getAttributeNames(); attributeNames.hasMoreElements(); ) {
            String attributeName = attributeNames.nextElement();
            getJedisCluster().hset(session.getId(), attributeName, standardSession.getAttribute(attributeName).toString());
        }
        keys.add(session.getId());
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }
}
