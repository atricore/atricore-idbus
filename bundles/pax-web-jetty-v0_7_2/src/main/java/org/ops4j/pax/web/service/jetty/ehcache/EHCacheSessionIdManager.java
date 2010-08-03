package org.ops4j.pax.web.service.jetty.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.SessionManager;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.servlet.AbstractSessionIdManager;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Random;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EHCacheSessionIdManager extends AbstractSessionIdManager {
    
    private static final org.apache.commons.logging.Log logger = LogFactory.getLog(EHCacheSessionIdManager.class);

    private Cache _sessions;

    protected final HashSet<String> _sessionIds = new HashSet();

    public EHCacheSessionIdManager(Server server, Cache _sessions) {
        super(server);
        this._sessions = _sessions;
    }

    public EHCacheSessionIdManager(Server server, Random random, Cache _sessions) {
        super(server, random);
        this._sessions = _sessions;
    }

    public boolean idInUse(String id) {

        if (id == null)
            return false;

        String clusterId = getClusterId(id);
        boolean inUse = false;

        if (logger.isDebugEnabled())
            logger.debug("INUSE session " + id);

        synchronized (_sessionIds)
        {
            inUse = _sessionIds.contains(clusterId);
        }

        if (inUse)
            return true; //optimisation - if this session is one we've been managing, we can check locally

        //otherwise, we need to go to the database to check
        try
        {
            return exists(clusterId);
        }
        catch (Exception e)
        {
            logger.warn("Problem checking inUse for id="+clusterId, e);
            return false;
        }
    }

    public void addSession(HttpSession session) {
        if (session == null)
            return;

        synchronized (_sessionIds)
        {
            String id = ((EHCacheSessionManager.EHCacheSession)session).getClusterId();

            if (logger.isDebugEnabled())
                logger.debug("ADD session " + id);

            try
            {
                insert(id);
                _sessionIds.add(id);
            }
            catch (Exception e)
            {
                logger.warn("Problem storing session id="+id, e);
            }
        }

    }

    public void removeSession(HttpSession session) {
        if (session == null)
            return;

        removeSession(((EHCacheSessionManager.EHCacheSession)session).getClusterId());

    }

    public void removeSession (String id)
    {

        if (id == null)
            return;

        synchronized (_sessionIds)
        {
            if (logger.isDebugEnabled())
                logger.debug("REMOVE session id="+id);
            try
            {
                _sessionIds.remove(id);
                delete(id);
            }
            catch (Exception e)
            {
                logger.warn("Problem removing session id="+id, e);
            }
        }

    }

    /**
     * Get the session id without any node identifier suffix.
     */
    public String getClusterId(String nodeId)
    {
        int dot=nodeId.lastIndexOf('.');
        return (dot>0)?nodeId.substring(0,dot):nodeId;
    }


    /**
     * Get the session id, including this node's id as a suffix.
     */
    public String getNodeId(String clusterId, HttpServletRequest request)
    {
        if (_workerName!=null)
            return clusterId+'.'+_workerName;

        return clusterId;
    }


    public void invalidateAll(String id) {
        //take the id out of the list of known sessionids for this node
        removeSession(id);

        synchronized (_sessionIds)
        {
            //tell all contexts that may have a session object with this id to
            //get rid of them
            Handler[] contexts = _server.getChildHandlersByClass(ContextHandler.class);
            for (int i=0; contexts!=null && i<contexts.length; i++)
            {
                SessionManager manager = ((SessionHandler)((ContextHandler)contexts[i]).getChildHandlerByClass(SessionHandler.class)).getSessionManager();

                if (manager instanceof EHCacheSessionManager)
                {
                    ((EHCacheSessionManager)manager).invalidateSession(id);
                }
            }
        }

    }

    protected boolean exists (String id) {
        return _sessions.isKeyInCache(id);
    }

    protected void insert (String id) {

        if (!exists(id)) {
            SessionData data = new SessionData(id);
            Element e = new Element(id, data);
            _sessions.put(e);
        }

    }

    protected boolean delete (String id) {
        return _sessions.remove(id);
    }



}
