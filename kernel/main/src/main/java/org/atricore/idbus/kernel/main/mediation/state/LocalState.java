package org.atricore.idbus.kernel.main.mediation.state;

import java.util.Collection;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface LocalState extends java.io.Serializable {

    String getId();

    Set<String> getAlternativeIds(String idName);

    void addAlternativeId(String idName, String id);

    void removeAlternativeIds(String idName);

    void removeAlternativeId(String idName, String id);

    Collection<String> getAlternativeIdNames();

    void setValue(String key, Object value);

    Object getValue(String key);

    void removeValue(String key);

    Collection<String> getKeys();

    boolean isNew();

}
