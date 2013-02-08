package org.atricore.idbus.kernel.monitoring.core;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;

public interface Request
{
    String getRequestURI();

    String getHeader(String key);

    String getRemoteUser();

    Set<String> getParameterNames();

    Collection<String> getParameterValues(String key);

    Object getAttribute(String key);
}