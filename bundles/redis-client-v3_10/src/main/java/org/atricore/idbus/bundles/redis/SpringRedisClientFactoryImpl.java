/*
 * Atricore IDBus
 *
 * Copyright (c) 2009-2024, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.atricore.idbus.bundles.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Spring-integrated Redis client factory implementation.
 * Manages connection pooling and lifecycle for Redis connections.
 * Compatible with OSGi/Karaf 2 environment.
 * 
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SpringRedisClientFactoryImpl 
        implements RedisClientFactory, 
                   InitializingBean, 
                   DisposableBean, 
                   ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(SpringRedisClientFactoryImpl.class);

    // Redis configuration
    private String host = "localhost";
    private int port = Protocol.DEFAULT_PORT; // 6379
    private String password;
    private int database = Protocol.DEFAULT_DATABASE; // 0
    private int connectionTimeout = Protocol.DEFAULT_TIMEOUT; // 2000ms
    private int socketTimeout = Protocol.DEFAULT_TIMEOUT; // 2000ms
    
    // Pool configuration
    private int maxTotal = 50;
    private int maxIdle = 10;
    private int minIdle = 2;
    private boolean testOnBorrow = true;
    private boolean testOnReturn = false;
    private boolean testWhileIdle = true;
    
    // Internal state
    private JedisPool jedisPool;
    private ApplicationContext applicationContext;
    private boolean initialized = false;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }

    /**
     * Initialize the Redis connection pool
     */
    public synchronized void init() {
        if (initialized) {
            return;
        }

        logger.info("Initializing Redis Client Factory");
        logger.info("Redis Host: " + host + ":" + port);
        logger.info("Redis Database: " + database);
        logger.info("Pool Max Total: " + maxTotal);
        logger.info("Pool Max Idle: " + maxIdle);

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            // Use application context classloader for OSGi compatibility
            Thread.currentThread().setContextClassLoader(
                applicationContext != null ? 
                    applicationContext.getClassLoader() : 
                    this.getClass().getClassLoader()
            );

            // Configure pool
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(maxTotal);
            poolConfig.setMaxIdle(maxIdle);
            poolConfig.setMinIdle(minIdle);
            poolConfig.setTestOnBorrow(testOnBorrow);
            poolConfig.setTestOnReturn(testOnReturn);
            poolConfig.setTestWhileIdle(testWhileIdle);

            // Create pool
            // JedisPool(GenericObjectPoolConfig poolConfig, String host, int port, 
            //           int connectionTimeout, String password, int database)
            if (password != null && !password.trim().isEmpty()) {
                jedisPool = new JedisPool(
                    poolConfig, 
                    host, 
                    port, 
                    connectionTimeout,
                    password, 
                    database
                );
                logger.info("Created Redis pool with authentication");
            } else {
                jedisPool = new JedisPool(
                    poolConfig, 
                    host, 
                    port, 
                    connectionTimeout,
                    null,
                    database
                );
                logger.info("Created Redis pool without authentication");
            }

            // Test connection
            try (Jedis jedis = jedisPool.getResource()) {
                String pong = jedis.ping();
                logger.info("Redis connection test: " + pong);
            }

            initialized = true;
            logger.info("Redis Client Factory initialized successfully");

        } catch (Exception e) {
            logger.error("Failed to initialize Redis Client Factory", e);
            throw new RuntimeException("Failed to initialize Redis Client Factory", e);
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    @Override
    public Jedis getConnection() {
        if (!initialized) {
            throw new IllegalStateException("RedisClientFactory not initialized");
        }
        
        if (jedisPool == null) {
            throw new IllegalStateException("JedisPool is null");
        }
        
        return jedisPool.getResource();
    }

    @Override
    public JedisPool getPool() {
        return jedisPool;
    }

    @Override
    public void returnConnection(Jedis connection) {
        if (connection != null) {
            connection.close(); // Returns to pool in Jedis 3.x
        }
    }

    @Override
    public boolean isAvailable() {
        if (!initialized || jedisPool == null) {
            return false;
        }
        
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String pong = jedis.ping();
            return "PONG".equals(pong);
        } catch (Exception e) {
            logger.warn("Redis availability check failed: " + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void shutdown() {
        if (initialized && jedisPool != null) {
            logger.info("Shutting down Redis Client Factory");
            try {
                jedisPool.close();
            } catch (Exception e) {
                logger.error("Error shutting down Redis pool", e);
            } finally {
                jedisPool = null;
                initialized = false;
            }
        }
    }

    // Getters and Setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }
}
