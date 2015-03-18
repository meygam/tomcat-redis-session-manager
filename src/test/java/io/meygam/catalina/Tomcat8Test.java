package io.meygam.catalina;

import io.meygam.catalina.session.RedisManager;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.embedded.RedisServer;

import java.io.File;
import java.io.IOException;

/**
 * Created by saravana on 3/16/15.
 */
public class Tomcat8Test {

    private Tomcat tomcat;
    private RedisServer redisServer;

    @Before
    public void setUp() throws Exception {
        startRedisServer();
        startTomcat();
    }

    private void startTomcat() throws LifecycleException, InterruptedException {
        Thread thread = new Thread() {
            public void run() {
                tomcat = new Tomcat();
                File base = new File(System.getProperty("java.io.tmpdir"));
                Context rootCtx = tomcat.addContext("/app", base.getAbsolutePath());
                Tomcat.addServlet(rootCtx, "sessionServlet", new SessionServlet());
                rootCtx.addServletMapping("/session", "sessionServlet");
                RedisManager redisManager = new RedisManager();
                redisManager.setRedisNodes("localhost");
                rootCtx.setManager(redisManager);
                try {
                    tomcat.start();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
                tomcat.getServer().await();
            }
        };
        thread.start();
        Thread.sleep(1000);
    }

    private void startRedisServer() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @Test
    public void saveSession() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/app/session");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        Assert.assertEquals(200, response1.getStatusLine().getStatusCode());
    }

    @After
    public void tearDown() throws Exception {
        tomcat.stop();
        redisServer.stop();
    }
}
