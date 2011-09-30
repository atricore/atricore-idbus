package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpFields;
import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.util.LazyList;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InternalHttpServletRequest extends Request {

    private static final Collection __defaultLocale = Collections.singleton(Locale.getDefault());

    private Cookie[] cookies;

    private HttpFields requestFields;

    private HttpSession session;

    private StringBuffer rootURL;

    public InternalHttpServletRequest() {

    }

    public void setRequestFields(HttpFields fields) {
        this.requestFields = fields;
    }

    @Override
    public long getContentRead() {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public void setContentType(String contentType) {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public long getDateHeader(String name) {
        return requestFields.getDateField(name);
    }

    @Override
    public String getHeader(String name) {
        return requestFields.getStringField(name);
    }

    @Override
    public Enumeration getHeaderNames() {
        return requestFields.getFieldNames();
    }

    @Override
    public Enumeration getHeaders(String name) {
        Enumeration e = requestFields.getValues(name);
        if (e == null)
            return Collections.enumeration(Collections.EMPTY_LIST);
        return e;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public int getIntHeader(String name) {
        return (int) requestFields.getLongField(name);
    }

    @Override
    public Locale getLocale() {
        Enumeration enm = requestFields.getValues(HttpHeaders.ACCEPT_LANGUAGE, HttpFields.__separators);

        // handle no locale
        if (enm == null || !enm.hasMoreElements())
            return Locale.getDefault();

        // sort the list in quality order
        List acceptLanguage = HttpFields.qualityList(enm);
        if (acceptLanguage.size()==0)
            return  Locale.getDefault();

        int size=acceptLanguage.size();

        // convert to locals
        for (int i=0; i<size; i++)
        {
            String language = (String)acceptLanguage.get(i);
            language=HttpFields.valueParameters(language,null);
            String country = "";
            int dash = language.indexOf('-');
            if (dash > -1)
            {
                country = language.substring(dash + 1).trim();
                language = language.substring(0,dash).trim();
            }
            return new Locale(language,country);
        }

        return  Locale.getDefault();
    }

    @Override
    public Enumeration getLocales() {
        Enumeration enm = requestFields.getValues(HttpHeaders.ACCEPT_LANGUAGE, HttpFields.__separators);

        // handle no locale
        if (enm == null || !enm.hasMoreElements())
            return Collections.enumeration(__defaultLocale);

        // sort the list in quality order
        List acceptLanguage = HttpFields.qualityList(enm);

        if (acceptLanguage.size()==0)
            return
            Collections.enumeration(__defaultLocale);

        Object langs = null;
        int size=acceptLanguage.size();

        // convert to locals
        for (int i=0; i<size; i++)
        {
            String language = (String)acceptLanguage.get(i);
            language=HttpFields.valueParameters(language,null);
            String country = "";
            int dash = language.indexOf('-');
            if (dash > -1)
            {
                country = language.substring(dash + 1).trim();
                language = language.substring(0,dash).trim();
            }
            langs= LazyList.ensureSize(langs, size);
            langs=LazyList.add(langs,new Locale(language,country));
        }

        if (LazyList.size(langs)==0)
            return Collections.enumeration(__defaultLocale);

        return Collections.enumeration(LazyList.getList(langs));

    }

    @Override
    public HttpSession getSession() {
        return session;
    }

    @Override
    public void setSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public void setCookies(Cookie[] cookies) {
        super.setCookies(cookies);
        this.cookies = cookies;
    }

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    @Override
    public HttpConnection getConnection() {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }


    public void setRootURL(StringBuffer rootURL) {
        this.rootURL = rootURL;
    }

    @Override
    public StringBuffer getRootURL() {
        return rootURL;
    }

    @Override
    public void addEventListener(EventListener listener) {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public void removeEventListener(EventListener listener) {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public void setRequestListeners(Object requestListeners) {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public Object takeRequestListeners() {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public void saveNewSession(Object key, HttpSession session) {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public HttpSession recoverNewSession(Object key) {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }

    @Override
    public ServletResponse getServletResponse() {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }
}