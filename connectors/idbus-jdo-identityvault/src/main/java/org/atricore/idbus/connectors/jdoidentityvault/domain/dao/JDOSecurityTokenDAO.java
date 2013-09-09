package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOSecurityToken;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface JDOSecurityTokenDAO extends GenericDAO<JDOSecurityToken, Long> {

    JDOSecurityToken findByTokenId(String tokenId);

    Collection<JDOSecurityToken> findByIssueInstantBefore(long issueInstant);

    Collection<JDOSecurityToken> findByExpiresOnBefore(long issueInstant);
}

