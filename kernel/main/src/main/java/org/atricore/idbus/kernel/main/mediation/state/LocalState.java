package org.atricore.idbus.kernel.main.mediation.state;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface LocalState extends java.io.Serializable {

    String getId();

    String getAlternativeId(String idName);

    void addAlternativeId(String idName, String id);

    void removeAlternativeId(String idName);

    Collection<String> getAlternativeIdNames();

    void setValue(String key, Object value);

    Object getValue(String key);

    void removeValue(String key);

    Collection<String> getKeys();




}
