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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Factory for Redis client connections.
 * Provides connection pooling and lifecycle management for Redis clients.
 * 
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface RedisClientFactory {
    
    /**
     * Get a Redis connection from the pool.
     * The connection MUST be closed after use to return it to the pool.
     * 
     * @return Jedis client instance (must be closed after use)
     */
    Jedis getConnection();
    
    /**
     * Get the underlying connection pool.
     * 
     * @return JedisPool instance
     */
    JedisPool getPool();
    
    /**
     * Return a connection to the pool.
     * This is an alias for connection.close() for clarity.
     * 
     * @param connection the connection to return
     */
    void returnConnection(Jedis connection);
    
    /**
     * Test if Redis is available and reachable.
     * 
     * @return true if Redis responds to PING command
     */
    boolean isAvailable();
    
    /**
     * Shutdown the connection pool and release all resources.
     */
    void shutdown();
}
