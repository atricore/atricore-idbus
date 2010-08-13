package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface JDOGroupDAO extends GenericDAO<JDOGroup, Long> {

    JDOGroup findByName(String name);
}
