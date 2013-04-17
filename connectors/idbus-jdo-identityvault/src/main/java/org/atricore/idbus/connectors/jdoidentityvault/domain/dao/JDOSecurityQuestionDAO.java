package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOSecurityQuestion;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/10/13
 */
public interface JDOSecurityQuestionDAO extends GenericDAO<JDOSecurityQuestion, Long> {

    JDOSecurityQuestion findByName(String name);


}

