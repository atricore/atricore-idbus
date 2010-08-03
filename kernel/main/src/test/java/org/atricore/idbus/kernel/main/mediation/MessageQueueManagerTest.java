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

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 *
 * @author <a href="mailto:ajadzinsky@atricore.org">Alejandro Jadzinsky</a>
 *         User: ajadzinsky
 *         Date: 17/03/2009
 *         Time: 18:40:25
 */
public class MessageQueueManagerTest {
    private static Log logger = LogFactory.getLog(MessageQueueManagerTest.class);

    private ActiveMQMessageQueueManager mqm;

    @Before
    public void initMessageQueueManager() throws Exception {
        mqm = new ActiveMQMessageQueueManager();

        mqm.setArtifactGenerator( new ArtifactGeneratorImpl() );
        ActiveMQConnectionFactory amq = new ActiveMQConnectionFactory();
        amq.setBrokerURL("tcp://localhost:61616");
        mqm.setConnectionFactory(amq);
        mqm.setJmsProviderDestinationName("Queue/JOSSO/Artifact");

        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistent(false);
        broker.start();

        mqm.init();
    }

    @Test
    public void testMessageQueueManager() throws Exception {
        String obj1 = "object 1";
        String obj2 = "object 2";
        String obj3 = "object 3";

        Artifact art1 = mqm.pushMessage(obj1);
        Artifact art2 = mqm.pushMessage(obj2);
        Artifact art3 = mqm.pushMessage(obj3);

        logger.debug("obj2: " + obj2);
        logger.debug("art2: " + art2.getContent());
        Object peek2 = mqm.peekMessage(art2);
        logger.debug("peek2: " + peek2);
        assert peek2.equals(obj2);

        Object pull2 = mqm.pullMessage(art2);
        logger.debug("pull2: " + pull2);
        assert pull2.equals(obj2);

        Object peek22 = mqm.peekMessage(art2);
        logger.debug("peek2: " + peek22);
        assert peek22 == null;
    }
}
