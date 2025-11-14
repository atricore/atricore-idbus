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
package org.atricore.idbus.bundles.redis.serialization;

/**
 * Interface for serializing/deserializing objects for Redis storage.
 * Implementations should be thread-safe.
 * 
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface RedisSerializer {
    
    /**
     * Serialize an object to byte array for storage in Redis.
     * 
     * @param obj the object to serialize
     * @return serialized byte array, or empty array if obj is null
     * @throws SerializationException if serialization fails
     */
    byte[] serialize(Object obj) throws SerializationException;
    
    /**
     * Deserialize byte array to object from Redis storage.
     * 
     * @param data the byte array to deserialize
     * @return deserialized object, or null if data is null/empty
     * @throws SerializationException if deserialization fails
     */
    Object deserialize(byte[] data) throws SerializationException;
    
    /**
     * Exception thrown during serialization/deserialization operations.
     */
    class SerializationException extends RuntimeException {
        
        public SerializationException(String message) {
            super(message);
        }
        
        public SerializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
