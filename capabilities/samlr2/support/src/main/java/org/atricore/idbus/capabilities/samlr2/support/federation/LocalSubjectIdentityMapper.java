package org.atricore.idbus.capabilities.samlr2.support.federation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.IdentityMapper;

import javax.security.auth.Subject;

/**
 *  The mapped subject contains local sujbect information
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LocalSubjectIdentityMapper implements IdentityMapper {

    private static final Log logger = LogFactory.getLog(LocalSubjectIdentityMapper.class);

    public Subject map(Subject remoteSubject, Subject localSubject) {

        return localSubject;
    }
}
