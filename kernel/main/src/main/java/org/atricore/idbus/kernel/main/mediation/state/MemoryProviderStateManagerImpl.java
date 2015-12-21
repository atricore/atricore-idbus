package org.atricore.idbus.kernel.main.mediation.state;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MemoryProviderStateManagerImpl implements ProviderStateManager {

    private static final Log logger = LogFactory.getLog(MemoryProviderStateManagerImpl.class);

    // TODO : Cleanup cache when expires!
    private Cache cache;

    private UUIDGenerator idGen = new UUIDGenerator(true);

    private String namespace;

    public MemoryProviderStateManagerImpl () {
        cache = new Cache(idGen.generateId());
        logger.debug("Created internal cache instance with ID " + cache.getId());
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void store(ProviderStateContext ctx, LocalState value) {
        cache.put(new Element(ctx.getProvider().getName(), value.getId(), value));
    }

    public LocalState retrieve(ProviderStateContext ctx, String stateId) {

        Element e = cache.get(new Key(ctx.getProvider().getName(), stateId));
        if (e != null)
            return e.getState();

        return null;
    }

    public LocalState retrieve(ProviderStateContext ctx, String keyName, String keyValue) {

        Element e = cache.get(new Key(ctx.getProvider().getName(), keyName, keyValue));
        if (e != null)
            return e.getState();

        return null;
    }

    public void remove(ProviderStateContext ctx, String key) {
        cache.remove(new Key(ctx.getProvider().getName(), key));
    }

    public LocalState createState(ProviderStateContext ctx) {

        LocalStateImpl state = new LocalStateImpl(idGen.generateId());
        store(ctx, state);

        if (logger.isDebugEnabled())
            logger.debug("Created new LocalState instance with Key " + state.getId());

        return state;
    }

    public Collection<LocalState> retrieveAll(ProviderStateContext ctx) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    protected class Cache {

        private String id;

        private Map<String, Map<String, Element>> alternativeKeys;

        private Map<String, Element> elements;

        public Cache(String id) {
            this.id = id;
            elements = new HashMap<String, Element>();
            alternativeKeys = new HashMap<String, Map<String,Element>>();
        }

        public String getId() {
            return id;
        }

        public void put(Element e) {

            elements.put(e.getKey().getValue(), e);

            if (logger.isTraceEnabled())
                logger.trace("PUT:" + e.getKey() + " " + e);

            for (Key alternativeKey : e.getAlternativeKeys()) {

                Map<String, Element> alternativeMap = alternativeKeys.get(alternativeKey.getQName());
                if ( alternativeMap == null) {
                    alternativeMap = new HashMap<String, Element>();
                    alternativeKeys.put(alternativeKey.getQName(), alternativeMap);
                }

                if (logger.isTraceEnabled())
                    logger.trace("PUT:"+alternativeKey + " " + e);

                alternativeMap.put(alternativeKey.getValue(), e);
            }

        }

        public Element get(Key key) {

            Element e = null;
            if (key.isPrimary()) {
                e = elements.get(key.getValue());
            } else {
                Map<String, Element> alternativeMap = alternativeKeys.get(key.getQName());
                if (alternativeMap != null) {
                    e = alternativeMap.get(key.getValue());
                }
            }

            if (logger.isTraceEnabled())
                logger.trace("GET:{" + key.getQName() + "}" + key.getValue()+ "=" + e);

            return e;
        }

        public void remove(Key key) {
            Element e = elements.get(key.getValue());

            if (logger.isTraceEnabled())
                logger.trace("REMOVE:" + key + "=" + e);

            if (e != null) {
                for (Key ak : e.getAlternativeKeys()) {
                    Map<String, Element> am = alternativeKeys.get(ak.getQName());
                    if (am != null)
                        am.remove(ak.getValue());
                }

                elements.remove(key.getValue());
            }
        }

    }

    protected class Element {

        private Key key;

        private Set<Key> alternativeKeys;

        private LocalState state;

        public Element(String ns, String id, LocalState state) {

            // Setup Keys
            this.key = new Key(ns, id);
            this.alternativeKeys = new HashSet<Key>();
            
            for (String keyName : state.getAlternativeIdNames()) {
                String alternativeId = state.getAlternativeId(keyName);
                Key alternativeKey = new Key(ns, keyName, alternativeId);
                alternativeKeys.add(alternativeKey);
            }
            this.state = state;
        }

        public Key getKey() {
            return key;
        }

        public Collection<Key> getAlternativeKeys() {
            return alternativeKeys;
        }

        public LocalState getState() {
            return state;
        }

        @Override
        public String toString() {
            return "ELEMENT : [key=" + key.toString() + ",stateId='"+(state != null ? state.getId() : "<NO-STATE>")+"']";
        }
    }

    protected class Key {

        private String nameSpace;

        private String value;

        private String qName;

        private boolean primary = true;

        public Key(String nameSpace, String name, String value) {
            this.nameSpace = nameSpace;
            this.value = value;
            this.qName = nameSpace + ":" + name;
            this.primary = false;
        }

        public Key(String nameSpace, String value) {
            this(nameSpace, "PRIMARY", value);
            this.primary = true;
        }

        public boolean isPrimary() {
            return primary;
        }

        public String getQName() {
            return qName;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "KEY : [qname='"+qName+"',value='"+value+"']";
        }

    }
}


