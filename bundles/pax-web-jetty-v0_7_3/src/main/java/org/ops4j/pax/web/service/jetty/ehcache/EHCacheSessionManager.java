package org.ops4j.pax.web.service.jetty.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.util.LazyList;
import org.ops4j.pax.web.service.spi.model.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EHCacheSessionManager extends AbstractSessionManager {
    
    private static final org.apache.commons.logging.Log logger = LogFactory.getLog(EHCacheSessionManager.class);

    private CacheManager _cacheManager;
    private Cache _sessionsCache;

    private ConcurrentHashMap _sessions;
    protected long _saveIntervalMillis = 100; //only persist changes to session access times every 60 secs

    public EHCacheSessionManager(Server server, Model model, Cache sessionsCache) {
        this._sessionsCache = sessionsCache;
    }

    /**
     * Session
     *
     * Session instance in memory of this node.
     */
    public class EHCacheSession extends AbstractSessionManager.Session
    {
        private final SessionData _data;
        private boolean _dirty=false;

        /**
         * Session from a request.
         * 
         * @param request
         */
        protected EHCacheSession (HttpServletRequest request)
        {
         
            super(request);   
            _data = new SessionData(_clusterId);
            _data.setMaxIdleMs(_dftMaxIdleSecs*1000);
            _data.setCanonicalContext(canonicalize(_context.getContextPath()));
            _data.setVirtualHost(getVirtualHost(_context));
            _data.setExpiryTime(_maxIdleMs < 0 ? 0 : (System.currentTimeMillis() + _maxIdleMs));
            _values=_data.getAttributeMap();
        }

        /**
          * Session restored in database.
          * @param data
          */
         protected EHCacheSession (SessionData data)
         {
             super(data.getCreated(), data.getId());
             _data=data;
             _data.setMaxIdleMs(_dftMaxIdleSecs*1000);
             _values=data.getAttributeMap();
         }
        
         @Override
        protected Map newAttributeMap()
         {
             return _data.getAttributeMap();
         }
         
         @Override
        public void setAttribute (String name, Object value)
         {
             super.setAttribute(name, value);
             _dirty=true;
         }

         @Override
        public void removeAttribute (String name)
         {
             super.removeAttribute(name); 
             _dirty=true;
         }
         
         @Override
        protected void cookieSet()
         {
             _data.setCookieSet(_data.getAccessed());
         }

        /** 
         * Entry to session.
         * Called by SessionHandler on inbound request and the session already exists in this node's memory.
         * 
         * @see AbstractSessionManager.Session#access(long)
         */
        @Override
        protected void access(long time)
        {
            super.access(time);
            _data.setLastAccessed(_data.getAccessed());
            _data.setAccessed(time);
            _data.setExpiryTime(_maxIdleMs < 0 ? 0 : (time + _maxIdleMs));
        }

        /** 
         * Exit from session
         * @see AbstractSessionManager.Session#complete()
         */
        @Override
        protected void complete()
        {
            super.complete();
            try
            {
                if (_dirty)
                { 
                    //The session attributes have changed, write to the db, ensuring
                    //http passivation/activation listeners called
                    willPassivate();
                    updateSession(_data);
                    didActivate();
                }
                else if ((_data.getAccessed() - _data.getLastSaved()) >= (getSaveInterval() ))
                {  
                    updateSessionAccessTime(_data);
                }
            }
            catch (Exception e)
            {
                logger.warn("Problem persisting changed session data id="+getId(), e);
            }
            finally
            {
                _dirty=false;
            }
        }
        
        @Override
        protected void timeout() throws IllegalStateException
        {
            if (logger.isDebugEnabled()) logger.debug("Timing out session id="+getClusterId());
            super.timeout();
        }

        // To provide visibility

        @Override
        protected void didActivate() {
            super.didActivate();
        }

        @Override
        protected String getClusterId() {
            return super.getClusterId();
        }

        @Override
        protected void willPassivate() {
            super.willPassivate();
        }

    }
    
    
    
    
    /**
     * ClassLoadingObjectInputStream
     *
     *
     */
    protected class ClassLoadingObjectInputStream extends ObjectInputStream
    {
        public ClassLoadingObjectInputStream(java.io.InputStream in) throws IOException
        {
            super(in);
        }

        public ClassLoadingObjectInputStream () throws IOException
        {
            super();
        }

        @Override
        public Class resolveClass (java.io.ObjectStreamClass cl) throws IOException, ClassNotFoundException
        {
            try
            {
                return Class.forName(cl.getName(), false, Thread.currentThread().getContextClassLoader());
            }
            catch (ClassNotFoundException e)
            {
                return super.resolveClass(cl);
            }
        }
    }
    
    


    /**
     * Set the time in milliseconds which is the interval between
     * saving the session access time to the database.
     * 
     * This is an optimization that prevents the database from
     * being overloaded when a session is accessed very frequently.
     * 
     * On session exit, if the session attributes have NOT changed,
     * the time at which we last saved the accessed
     * time is compared to the current accessed time. If the interval
     * is at least saveIntervalSecs, then the access time will be
     * persisted to the database.
     * 
     * If any session attribute does change, then the attributes and
     * the accessed time are persisted.
     * 
     * @param millis
     */
    public void setSaveInterval (long millis)
    {
        _saveIntervalMillis =millis;
    }
  
    public long getSaveInterval ()
    {
        return _saveIntervalMillis;
    }

   
    
    /**
     * A method that can be implemented in subclasses to support
     * distributed caching of sessions. This method will be
     * called whenever the session is written to the database
     * because the session data has changed.
     * 
     * This could be used eg with a JMS backplane to notify nodes
     * that the session has changed and to delete the session from
     * the node's cache, and re-read it from the database.
     * @param session
     */
    public void cacheInvalidate (Session session)
    {
        
    }
    
    
    /** 
     * A session has been requested by it's id on this node.
     * 
     * Load the session by id AND context path from the database.
     * Multiple contexts may share the same session id (due to dispatching)
     * but they CANNOT share the same contents.
     * 
     * Check if last node id is my node id, if so, then the session we have
     * in memory cannot be stale. If another node used the session last, then
     * we need to refresh from the db.
     * 
     * NOTE: this method will go to the database, so if you only want to check 
     * for the existence of a Session in memory, use _sessions.get(id) instead.
     * 
     * @see AbstractSessionManager#getSession(java.lang.String)
     */
    @Override
    public EHCacheSession getSession(String idInCluster)
    {
        EHCacheSession session = (EHCacheSession)_sessions.get(idInCluster);
        
        synchronized (this)
        {        
            try
            {                
                //check if we need to reload the session - 
                //as an optimization, don't reload on every access
                //to reduce the load on the database. This introduces a window of 
                //possibility that the node may decide that the session is local to it,
                //when the session has actually been live on another node, and then
                //re-migrated to this node. This should be an extremely rare occurrence,
                //as load-balancers are generally well-behaved and consistently send 
                //sessions to the same node, changing only iff that node fails. 
                SessionData data = null;
                long now = System.currentTimeMillis();
                if (logger.isDebugEnabled()) logger.debug("now="+now+
                        " lastSaved="+(session==null?0:session._data.getLastSaved())+
                        " interval="+(_saveIntervalMillis )+
                        " difference="+(now - (session==null?0:session._data.getLastSaved())));
                
                if (session==null || ((now - session._data.getLastSaved()) >= (_saveIntervalMillis )))
                {       
                    data = loadSession(idInCluster, canonicalize(_context.getContextPath()), getVirtualHost(_context));
                }
                else
                {
                    data = session._data;
                }
                
                if (data != null)
                {
                    if (!data.getLastNode().equals(getIdManager().getWorkerName()) || session==null)
                    {
                        
                        //if the session in the database has not already expired
                        if (data.getExpiryTime() > System.currentTimeMillis())
                        {
                            //session last used on a different node, or we don't have it in memory
                            session = new EHCacheSession(data);
                            _sessions.put(idInCluster, session);
                            session.didActivate();
                            //TODO is this the best way to do this? Or do this on the way out using
                            //the _dirty flag?
                            updateSessionNode(data);
                        }
                    }
                    else
                        if (logger.isDebugEnabled()) logger.debug("Session not stale "+session._data);
                    //session in db shares same id, but is not for this context
                }
                else
                {
                    //No session in db with matching id and context path.
                    session=null;
                    if (logger.isDebugEnabled()) logger.debug("No session in database matching id="+idInCluster);
                }
                
                return session;
            }
            catch (Exception e)
            {
                logger.warn("Unable to load session from database", e);
                return null;
            }
        }
    }

   
    /** 
     * Get all the sessions as a map of id to Session.
     */
    @Override
    public Map getSessionMap()
    {
       return Collections.unmodifiableMap(_sessions);
    }

    
    /** 
     * Get the number of sessions.
     * 
     * @see AbstractSessionManager#getSessions()
     */
    @Override
    public int getSessions()
    {
        int size = 0;
        synchronized (this)
        {
            size = _sessions.size();
        }
        return size;
    }


    /** 
     * Start the session manager.
     * 
     * @see AbstractSessionManager#doStart()
     */
    @Override
    public void doStart() throws Exception
    {
        if (_sessionIdManager == null)
            throw new IllegalStateException("No session id manager defined");

        if (_sessionsCache  == null)
            throw new IllegalStateException("No cache manager defined");

        _sessions = new ConcurrentHashMap();
        super.doStart();
    }

    /** 
     * Stop the session manager.
     * 
     * @see AbstractSessionManager#doStop()
     */
    @Override
    public void doStop() throws Exception
    {
        _sessions.clear();
        _sessions = null;

        super.doStop();
    } 
    
    @Override
    protected void invalidateSessions()
    {
        //Do nothing - we don't want to remove and
        //invalidate all the sessions because this
        //method is called from doStop(), and just
        //because this context is stopping does not
        //mean that we should remove the session from
        //any other nodes
    }

    
    /**
     * Invalidate a session.
     * 
     * @param idInCluster
     */
    protected void invalidateSession (String idInCluster)
    {
        EHCacheSession session = null;

        if (logger.isDebugEnabled())
            logger.debug("INVALIDATING session " + idInCluster);

        synchronized (this)
        {
            session = (EHCacheSession)_sessions.get(idInCluster);
        }
        
        if (session != null)
        {
            session.invalidate();
        }
    }
   
    /** 
     * Delete an existing session, both from the in-memory map and
     * the database.
     * 
     * @see AbstractSessionManager#removeSession(java.lang.String)
     */
    @Override
    protected void removeSession(String idInCluster)
    {
        EHCacheSession session = null;

        if (logger.isDebugEnabled())
            logger.debug("REMOVING session " + idInCluster);

        synchronized (this)
        {
            session = (EHCacheSession)_sessions.remove(idInCluster);
        }
        try
        {
            if (session != null)
                deleteSession(session._data);
        }
        catch (Exception e)
        {
            logger.warn("Problem deleting session id="+idInCluster, e);
        }
    }


    /** 
     * Add a newly created session to our in-memory list for this node and persist it.
     */
    @Override
    protected void addSession(AbstractSessionManager.Session s)
    {
        if (s==null)
            return;

        EHCacheSession session = (EHCacheSession) s;
        if (logger.isDebugEnabled())
            logger.debug("ADDING session " + s);

        synchronized (this)
        {
            _sessions.put(session.getClusterId(), session);
        }
        
        //TODO or delay the store until exit out of session? If we crash before we store it
        //then session data will be lost.
        try
        {
            session.willPassivate();
            storeSession(((EHCacheSessionManager.EHCacheSession)session)._data);
            session.didActivate();
        }
        catch (Exception e)
        {
            logger.warn("Unable to store new session id="+session.getId() , e);
        }
    }


    /** 
     * Make a new Session.
     * 
     * @see AbstractSessionManager#newSession(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected AbstractSessionManager.Session newSession(HttpServletRequest request)
    {
        return new EHCacheSession(request);
    }

    /* ------------------------------------------------------------ */
    /** Remove session from manager 
     */
    @Override
    public void removeSession(AbstractSessionManager.Session s, boolean invalidate)
    {
        // Remove session from context and global maps
        boolean removed = false;

        EHCacheSession session = (EHCacheSession) s;
        
        synchronized (this)
        {
            //take this session out of the map of sessions for this context
            if (getSession(session.getClusterId()) != null)
            {
                removed = true;
                removeSession(session.getClusterId());
            }
        }

        if (removed)
        {
            // Remove session from all context and global id maps
            _sessionIdManager.removeSession(session);
            
            if (invalidate)
                _sessionIdManager.invalidateAll(session.getClusterId());
            
            if (invalidate && _sessionListeners!=null)
            {
                HttpSessionEvent event=new HttpSessionEvent(session);
                for (int i= LazyList.size(_sessionListeners); i-->0;)
                    ((HttpSessionListener)LazyList.get(_sessionListeners,i)).sessionDestroyed(event);
            }
            if (!invalidate)
            {
                session.willPassivate();
            }
        }
    }
    
    
    /**
     * Expire any Sessions we have in memory matching the list of
     * expired Session ids.
     * 
     * @param sessionIds
     */
    protected void expire (List sessionIds)
    { 
        //don't attempt to scavenge if we are shutting down
        if (isStopping() || isStopped())
            return;

        //Remove any sessions we already have in memory that match the ids
        Thread thread=Thread.currentThread();
        ClassLoader old_loader=thread.getContextClassLoader();
        ListIterator itor = sessionIds.listIterator();

        try
        {
            while (itor.hasNext())
            {
                String sessionId = (String)itor.next();
                if (logger.isDebugEnabled()) logger.debug("Expiring session id "+sessionId);
                
                EHCacheSession session = (EHCacheSession)_sessions.get(sessionId);
                if (session != null)
                {
                    session.timeout();
                    itor.remove();
                }
                else
                {
                    if (logger.isDebugEnabled()) logger.debug("Unrecognized session id="+sessionId);
                }
            }
        }
        catch (Throwable t)
        {
            if (t instanceof ThreadDeath)
                throw ((ThreadDeath)t);
            else
                logger.warn("Problem expiring sessions", t);
        }
        finally
        {
            thread.setContextClassLoader(old_loader);
        }
    }
    
    /**
     * Load a session from the database
     * @param id
     * @return the session data that was loaded
     * @throws Exception
     */
    protected SessionData loadSession (String id, String canonicalContextPath, String vhost)
    throws Exception
    {
        SessionData data = null;
        // String key = vhost + "_"+ canonicalContextPath + "_" + id;
        String key = id;

        if (logger.isDebugEnabled())
            logger.debug("LOADING session : " + key);

        Element sessionElement = _sessionsCache.get(key);

        if (sessionElement != null) {
            data = (SessionData) sessionElement.getValue();
            if (logger.isDebugEnabled())
               logger.debug("LOADED session "+data);
        }
        return data;
    }
    
    /**
     * Insert a session into the database.
     * 
     * @param data
     * @throws Exception
     */
    protected void storeSession (SessionData data)
    throws Exception
    {
        if (data==null)
            return;

        if (logger.isDebugEnabled())
            logger.debug("STORING session " + data);

        
        String rowId = calculateRowId(data);
        long now = System.currentTimeMillis();
        String nodeId = getIdManager().getWorkerName();

        data.setRowId(rowId); //set it on the in-memory data as well as in db
        data.setLastSaved(now);
        data.setLastNode(nodeId);

        // String key = data.getVirtualHost() + "_"+ data.getCanonicalContext() + "_" + data.getId();
        String key = data.getId();

        Element sessionElement = new Element(key, data);
        _sessionsCache.put(sessionElement);

        if (logger.isDebugEnabled())
            logger.debug("STORED session "+data);
    }
    
    
    /**
     * Update data on an existing persisted session.
     * 
     * @param data
     * @throws Exception
     */
    protected void updateSession (SessionData data)
    throws Exception
    {
        if (data==null)
            return;

        if (logger.isDebugEnabled())
            logger.debug("UPDATING session " + data);

        
        long now = System.currentTimeMillis();
        String nodeId = getIdManager().getWorkerName();

        data.setLastSaved(now);
        data.setLastNode(nodeId);

        // String key = data.getVirtualHost() + "_"+ data.getCanonicalContext() + "_" + data.getId();
        String key = data.getId();

        Element sessionElement = new Element(key, data);

        _sessionsCache.put(sessionElement);
        data.setLastSaved(now);
        if (logger.isDebugEnabled())
            logger.debug("Updated session "+data);
    }
    
    
    /**
     * Update the node on which the session was last seen to be my node.
     * 
     * @param data
     * @throws Exception
     */
    protected void updateSessionNode (SessionData data)
    throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug("UPDATE NODE session " + data);

        String nodeId = getIdManager().getWorkerName();
        data.setLastNode(nodeId);
        // String key = data.getVirtualHost() + "_"+ data.getCanonicalContext() + "_" + data.getId();
        String key = data.getId();

        Element sessionElement = new Element(key, data);
        _sessionsCache.put(sessionElement);

    }
    
    /**
     * Persist the time the session was last accessed.
     * 
     * @param data
     * @throws Exception
     */
    private void updateSessionAccessTime (SessionData data)
    throws Exception
    {
        long now = System.currentTimeMillis();
        String nodeId = getIdManager().getWorkerName();

        data.setLastNode(nodeId);
        data.setLastSaved(now);

        // String key = data.getVirtualHost() + "_"+ data.getCanonicalContext() + "_" + data.getId();
        String key = data.getId();

        Element sessionElement = new Element(key, data);

        _sessionsCache.put(sessionElement);

        if (logger.isDebugEnabled())
            logger.debug("Updated access time session id="+data.getId());

    }
    
    
    
    
    /**
     * Delete a session from the database. Should only be called
     * when the session has been invalidated.
     * 
     * @param data
     * @throws Exception
     */
    protected void deleteSession (SessionData data)
    throws Exception
    {
        //String key = data.getVirtualHost() + "_"+ data.getCanonicalContext() + "_" + data.getId();
        String key = data.getId();
        _sessionsCache.remove(key);

    }
    
    /**
     * Calculate a unique id for this session across the cluster.
     * 
     * Unique id is composed of: contextpath_virtualhost0_sessionid
     * @param data
     * @return
     */
    private String calculateRowId (SessionData data)
    {
        String rowId = canonicalize(_context.getContextPath());
        rowId = rowId + "_" + getVirtualHost(_context);
        rowId = rowId+"_"+data.getId();
        return rowId;
    }
    
    /**
     * Get the first virtual host for the context.
     * 
     * Used to help identify the exact session/contextPath.
     * 
     * @return 0.0.0.0 if no virtual host is defined
     */
    private String getVirtualHost (ContextHandler.SContext context)
    {
        String vhost = "0.0.0.0";
        
        if (context==null)
            return vhost;
        
        String [] vhosts = context.getContextHandler().getVirtualHosts();
        if (vhosts==null || vhosts.length==0 || vhosts[0]==null)
            return vhost;
        
        return vhosts[0];
    }
    
    /**
     * Make an acceptable file name from a context path.
     * 
     * @param path
     * @return
     */
    private String canonicalize (String path)
    {
        if (path==null)
            return "";
        
        return path.replace('/', '_').replace('.','_').replace('\\','_');
    }
}
