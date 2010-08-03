/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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

package org.atricore.idbus.kernel.main.mediation;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.JMSExceptionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundlespaceClassLoader;
import org.atricore.idbus.kernel.main.mediation.osgi.ClassLoadingAwareObjectInputStream;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;

import javax.jms.*;
import java.io.*;
import java.util.Enumeration;
import java.util.zip.InflaterInputStream;

/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1359 $ $Date: 2009-07-19 13:57:57 -0300 (Sun, 19 Jul 2009) $
 */
public interface MessageQueueManager  {


    public ConnectionFactory getConnectionFactory() ;

    public String getJmsProviderDestinationName() ;

    public void init() throws Exception ;

    public Object pullMessage(Artifact artifact) throws Exception ;

    public Object peekMessage(Artifact artifact) throws Exception ;

    public Artifact pushMessage(Object content) throws Exception ;

    public void shutDown() throws Exception ;

    public ArtifactGenerator getArtifactGenerator() ;


}