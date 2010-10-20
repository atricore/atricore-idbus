package org.ops4j.pax.web.service.jetty.wadi;

import org.codehaus.wadi.core.contextualiser.InvocationException;
import org.codehaus.wadi.web.impl.WebInvocation;
import org.mortbay.jetty.*;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.log.Log;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiWadiSessionHandler extends SessionHandler
{
	public OsgiWadiSessionHandler(SessionManager sessionManager)
	{
        super(sessionManager);
	}

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException
    {
        setRequestedId(request, dispatch);

        WadiClusteredInvocation invocation = new WadiClusteredInvocation(target,request,response,dispatch);
        try
        {
            invocation.invoke();
        }
        catch (Exception e)
        {
            Log.warn(e);
            Throwable cause = e.getCause();
            if (cause instanceof HttpException)
            {
                throw (HttpException) cause;
            }
            else if (cause instanceof IOException)
            {
                throw (IOException) cause;
            }
            else
            {
                throw (IOException) new IOException().initCause(cause);
            }
        }

    }

	protected class WadiClusteredInvocation
    {
        protected final String target;
        protected final HttpServletRequest request;
        protected final HttpServletResponse response;
        protected final int dispatch;

        protected WadiClusteredInvocation(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) {
            this.target = target;
            this.request = request;
            this.response = response;
            this.dispatch = dispatch;
        }

        public void invoke() throws Exception
        {
            WebInvocation invocation = new WebInvocation();
            invocation.setDoNotExecuteOnEndProcessing(true);
            FilterChain chainAdapter = new FilterChain() {
                public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException
                {
                    try
                    {
                        invokeLocally();
                    }
                    catch (Exception e)
                    {
                        throw (IOException) new IOException().initCause(e);
                    }
                }
            };
            invocation.init(request, response, chainAdapter);
            try
            {
                OsgiWadiSessionManager wSessionManager = (OsgiWadiSessionManager)getSessionManager();
                wSessionManager.getClusteredManager().contextualise(invocation);
            }
            catch (InvocationException e) {
                Throwable throwable = e.getCause();
                if (throwable instanceof IOException)
                {
                    throw new Exception(throwable);
                }
                else if
                (throwable instanceof ServletException)
                {
                    throw new Exception(throwable);
                }
                else
                {
                    throw new Exception(e);
                }
            }
        }

        protected void invokeLocally() throws  Exception
        {
            OsgiWadiSessionHandler.super.handle(target, request, response, dispatch);
        }

        public String getRequestedSessionId()
        {
            return request.getRequestedSessionId();
        }
    }

}
