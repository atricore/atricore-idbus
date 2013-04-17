package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOSecurityQuestion;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOSecurityQuestionDAO;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/10/13
 */
public class JDOSecurityQuestionDAOImpl extends GenericDAOImpl<JDOSecurityQuestion, Long> implements JDOSecurityQuestionDAO {

    private static final Log logger = LogFactory.getLog(JDOSecurityQuestionDAOImpl.class);

    public JDOSecurityQuestion findByName(String name) {

        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOSecurityQuestion" +
                " WHERE this.name == '" + name + "'");

        Collection<JDOSecurityQuestion> securityQuestions = (Collection<JDOSecurityQuestion>) query.execute();
        if (securityQuestions == null || securityQuestions.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, securityQuestions.size());

        return securityQuestions.iterator().next();
    }


}
