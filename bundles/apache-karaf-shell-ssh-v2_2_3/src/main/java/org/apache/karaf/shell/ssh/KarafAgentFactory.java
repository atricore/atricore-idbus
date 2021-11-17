/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.shell.ssh;

import org.apache.sshd.agent.SshAgent;
import org.apache.sshd.agent.SshAgentFactory;
import org.apache.sshd.agent.SshAgentServer;
import org.apache.sshd.agent.common.AgentDelegate;
import org.apache.sshd.agent.local.AgentServerProxy;
import org.apache.sshd.agent.local.ChannelAgentForwarding;
import org.apache.sshd.common.Channel;
import org.apache.sshd.common.FactoryManager;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.Session;
import org.apache.sshd.common.session.ConnectionService;
import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KarafAgentFactory implements SshAgentFactory {

    private final Map<String, AgentServerProxy> proxies = new ConcurrentHashMap<String, AgentServerProxy>();
    private final Map<String, SshAgent> locals = new ConcurrentHashMap<String, SshAgent>();

    public NamedFactory<Channel> getChannelForwardingFactory() {
        return new ChannelAgentForwarding.Factory();
    }

    public SshAgent createClient(FactoryManager manager) throws IOException {
        String proxyId = manager.getProperties().get(SshAgent.SSH_AUTHSOCKET_ENV_NAME);
        if (proxyId == null) {
            throw new IllegalStateException("No " + SshAgent.SSH_AUTHSOCKET_ENV_NAME + " environment variable set");
        }
        AgentServerProxy proxy = proxies.get(proxyId);
        if (proxy != null) {
            return proxy.createClient();
        }
        SshAgent agent = locals.get(proxyId);
        if (agent != null) {
            return new AgentDelegate(agent);
        }
        throw new IllegalStateException("No ssh agent found");
    }

    public SshAgentServer createServer(ConnectionService service) throws IOException {
        Session session = service.getSession();
        if (!(session instanceof ServerSession)) {
            throw new IllegalStateException("The session used to create an agent server proxy must be a server session");
        }
        final AgentServerProxy proxy = new AgentServerProxy(service);
        proxies.put(proxy.getId(), proxy);
        return new SshAgentServer() {
            public String getId() {
                return proxy.getId();
            }
            public void close() {
                proxies.remove(proxy.getId());
                proxy.close();
            }
        };
    }

    public void registerAgent(SshAgent agent, Map<String, ?> properties) {
        if (agent != null) {
            Object id = properties.get("id");
            if (id == null) {
                throw new IllegalStateException("Local agent can't be registered with no 'id' property");
            }
            locals.put(id.toString(), agent);
        }
    }

    public void unregisterAgent(SshAgent agent, Map<String, ?> properties) {
        if (agent != null) {
            Object id = properties.get("id");
            if (id == null) {
                throw new IllegalStateException("Local agent can't be unregistered with no 'id' property");
            }
            locals.remove(id.toString());
        }
    }

}
