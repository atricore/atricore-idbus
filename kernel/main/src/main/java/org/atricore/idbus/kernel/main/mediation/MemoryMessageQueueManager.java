package org.atricore.idbus.kernel.main.mediation;

import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class MemoryMessageQueueManager implements MessageQueueManager {

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    // TODO : Purge old artifacts that no one claimed! (see Artifact.creationTime)
    private Map<String, Object> msgs = new ConcurrentHashMap<String, Object>();

    public ConnectionFactory getConnectionFactory() {
        return null;
    }

    public String getJmsProviderDestinationName() {
        return null;
    }

    public void init() throws Exception {
        msgs = new HashMap<String, Object>();
    }

    public synchronized Object pullMessage(Artifact artifact) throws Exception {
        return msgs.remove(artifact.getContent());

    }

    public synchronized Object peekMessage(Artifact artifact) throws Exception {
        return msgs.get(artifact.getContent());
    }

    public synchronized Artifact pushMessage(Object msg) throws Exception {
        ArtifactImpl a = new ArtifactImpl(uuidGenerator.generateId());
        msgs.put(a.getContent(), msg);
        return a;
    }

    public void shutDown() throws Exception {
        msgs.clear();
    }

    public ArtifactGenerator getArtifactGenerator() {
        return null;
    }
}
