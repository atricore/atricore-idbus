package org.ops4j.pax.web.service.jetty.ehcache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SessionData implements java.io.Serializable
{
    private final String _id;
    private String _rowId;
    private long _accessed;
    private long _lastAccessed;
    private long _maxIdleMs;
    private long _cookieSet;
    private long _created;
    private Map _attributes;
    private String _lastNode;
    private String _canonicalContext;
    private long _lastSaved;
    private long _expiryTime;
    private String _virtualHost;

    public SessionData (String sessionId)
    {
        _id=sessionId;
        _created=System.currentTimeMillis();
        _accessed = _created;
        _attributes = new ConcurrentHashMap();
    }

    public synchronized String getId ()
    {
        return _id;
    }

    public synchronized long getCreated ()
    {
        return _created;
    }

    protected synchronized void setCreated (long ms)
    {
        _created = ms;
    }

    public synchronized long getAccessed ()
    {
        return _accessed;
    }

    protected synchronized void setAccessed (long ms)
    {
        _accessed = ms;
    }


    public synchronized void setMaxIdleMs (long ms)
    {
        _maxIdleMs = ms;
    }

    public synchronized long getMaxIdleMs()
    {
        return _maxIdleMs;
    }

    public synchronized void setLastAccessed (long ms)
    {
        _lastAccessed = ms;
    }

    public synchronized long getLastAccessed()
    {
        return _lastAccessed;
    }

    public void setCookieSet (long ms)
    {
        _cookieSet = ms;
    }

    public synchronized long getCookieSet ()
    {
        return _cookieSet;
    }

    public synchronized void setRowId (String rowId)
    {
        _rowId=rowId;
    }

    protected synchronized String getRowId()
    {
        return _rowId;
    }

    protected synchronized Map getAttributeMap ()
    {
        return _attributes;
    }

    protected synchronized void setAttributeMap (ConcurrentHashMap map)
    {
        _attributes = map;
    }

    public synchronized void setLastNode (String node)
    {
        _lastNode=node;
    }

    public synchronized String getLastNode ()
    {
        return _lastNode;
    }

    public synchronized void setCanonicalContext(String str)
    {
        _canonicalContext=str;
    }

    public synchronized String getCanonicalContext ()
    {
        return _canonicalContext;
    }

    public synchronized long getLastSaved ()
    {
        return _lastSaved;
    }

    public synchronized void setLastSaved (long time)
    {
        _lastSaved=time;
    }

    public synchronized void setExpiryTime (long time)
    {
        _expiryTime=time;
    }

    public synchronized long getExpiryTime ()
    {
        return _expiryTime;
    }

    public synchronized void setVirtualHost (String vhost)
    {
        _virtualHost=vhost;
    }

    public synchronized String getVirtualHost ()
    {
        return _virtualHost;
    }

    @Override
    public String toString ()
    {
        return "Session rowId="+_rowId+",id="+_id+",lastNode="+_lastNode+
                        ",created="+_created+",accessed="+_accessed+
                        ",lastAccessed="+_lastAccessed+",cookieSet="+_cookieSet+
                        "lastSaved="+_lastSaved;
    }
}
