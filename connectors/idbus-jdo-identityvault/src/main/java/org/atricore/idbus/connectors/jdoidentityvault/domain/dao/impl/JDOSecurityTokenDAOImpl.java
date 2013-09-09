package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOSecurityToken;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOSecurityTokenDAO;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOSecurityTokenDAOImpl extends GenericDAOImpl<JDOSecurityToken, Long> implements JDOSecurityTokenDAO {

    public JDOSecurityToken findByTokenId(String tokenId) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOSecurityToken" +
                " WHERE this.tokenId == '" + tokenId + "'");

        Collection<JDOSecurityToken> tokens = (Collection<JDOSecurityToken>) query.execute();
        if (tokens == null || tokens.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, tokens.size());

        return tokens.iterator().next();
    }

    public Collection<JDOSecurityToken> findByIssueInstantBefore(long issueInstant) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOSecurityToken" +
                " WHERE this.issueInstant < " + issueInstant );

        Collection<JDOSecurityToken> tokens = (Collection<JDOSecurityToken>) query.execute();

        return tokens;
    }

    public Collection<JDOSecurityToken> findByExpiresOnBefore(long expiresOn) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOSecurityToken" +
                " WHERE this.expiresOn < " + expiresOn );

        Collection<JDOSecurityToken> tokens = (Collection<JDOSecurityToken>) query.execute();

        return tokens;
    }
}
