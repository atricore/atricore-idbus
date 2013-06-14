package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAcl;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAclEntry;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public interface JDOAclDAO extends GenericDAO<JDOAcl, Long> {

    JDOAcl findByName(String name);
}
