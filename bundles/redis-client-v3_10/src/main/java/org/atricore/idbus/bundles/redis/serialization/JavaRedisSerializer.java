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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.*;

/**
 * Java serialization implementation for Redis.
 * Uses standard Java ObjectInputStream/ObjectOutputStream.
 * Thread-safe and compatible with existing JOSSO serializable objects.
 * 
 * This implementation handles OSGi classloader issues by using the
 * application context classloader when available.
 * 
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JavaRedisSerializer implements RedisSerializer, ApplicationContextAware {
    
    private static final Log logger = LogFactory.getLog(JavaRedisSerializer.class);
    
    private ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        if (obj == null) {
            return new byte[0];
        }
        
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            // Use application context classloader for OSGi compatibility
            if (applicationContext != null) {
                Thread.currentThread().setContextClassLoader(
                    applicationContext.getClassLoader()
                );
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            
            byte[] data = baos.toByteArray();
            
            if (logger.isTraceEnabled()) {
                logger.trace("Serialized object of type " + obj.getClass().getName() + 
                           ", size: " + data.length + " bytes");
            }
            
            return data;
            
        } catch (IOException e) {
            String msg = "Failed to serialize object of type: " + obj.getClass().getName();
            logger.error(msg, e);
            throw new SerializationException(msg, e);
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }
    
    @Override
    public Object deserialize(byte[] data) throws SerializationException {
        if (data == null || data.length == 0) {
            return null;
        }
        
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            // Use application context classloader for OSGi compatibility
            if (applicationContext != null) {
                Thread.currentThread().setContextClassLoader(
                    applicationContext.getClassLoader()
                );
            }
            
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            
            if (logger.isTraceEnabled()) {
                logger.trace("Deserialized object of type " + obj.getClass().getName() + 
                           ", from " + data.length + " bytes");
            }
            
            return obj;
            
        } catch (IOException e) {
            String msg = "Failed to deserialize object from " + data.length + " bytes";
            logger.error(msg, e);
            throw new SerializationException(msg, e);
        } catch (ClassNotFoundException e) {
            String msg = "Failed to deserialize object from " + data.length + 
                        " bytes - class not found: " + e.getMessage();
            logger.error(msg, e);
            throw new SerializationException(msg, e);
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }
}
