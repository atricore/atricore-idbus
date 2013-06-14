package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAclEntry;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup;

import java.util.Collection;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public interface JDOAclEntryDAO extends GenericDAO<JDOAclEntry, Long> {

    JDOAclEntry findByFrom(String from);

    JDOAclEntry findByApprovalToken(String approvalToken);
}
