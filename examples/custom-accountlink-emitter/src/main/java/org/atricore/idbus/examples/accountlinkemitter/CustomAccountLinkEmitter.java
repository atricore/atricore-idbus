package org.atricore.idbus.examples.accountlinkemitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.kernel.main.federation.*;

import javax.security.auth.Subject;
import java.util.Map;
import java.util.Set;

/**
 * Created by sgonzalez on 3/16/15.
 */
public class CustomAccountLinkEmitter implements AccountLinkEmitter {

    private static final Log logger = LogFactory.getLog(CustomAccountLinkEmitter.class);

    @Override
    public AccountLink emit(Subject subject) {
        return emit(subject, null);
    }

    /**
     * Emit an AccountLink for the remote Subject
     * @param subject received from the IdP
     *
     * @return the new AccountLink instance
     */
    @Override
    public AccountLink emit(Subject subject, Object ctx) {
        Set<SubjectNameID> subjectNameIDs = subject.getPrincipals( SubjectNameID.class );
        if ( logger.isDebugEnabled() )
            logger.debug( "Principals found: " + subjectNameIDs.size() );

        for ( SubjectNameID subjectNameID : subjectNameIDs ) {

            if ( logger.isDebugEnabled()) {
                logger.debug( "Principal Name: " + subjectNameID.getName() );
                logger.debug( "Principal Format: " + subjectNameID.getFormat() );
            }

            if ( subjectNameID.getFormat() != null ) {
                NameIDFormat fmt = NameIDFormat.asEnum( subjectNameID.getFormat() );
                switch ( fmt ) {
                    case UNSPECIFIED:
                        return new DynamicAccountLinkImpl( subject,
                                subjectNameID.getName(),
                                NameIDFormat.UNSPECIFIED.getValue(),
                                ctx instanceof Map ? (Map<String, String>) ctx : null);

                    case EMAIL:
                        return new DynamicAccountLinkImpl( subject,
                                subjectNameID.getName(),
                                NameIDFormat.EMAIL.getValue(),
                                ctx instanceof Map ? (Map<String, String>) ctx : null);

                    case TRANSIENT:
                        return new DynamicAccountLinkImpl( subject,
                                subjectNameID.getName(),
                                NameIDFormat.TRANSIENT.getValue(),
                                ctx instanceof Map ? (Map<String, String>) ctx : null);

                    case PERSISTENT:
                        return new DynamicAccountLinkImpl( subject,
                                subjectNameID.getName(),
                                NameIDFormat.PERSISTENT.getValue(),
                                ctx instanceof Map ? (Map<String, String>) ctx : null);

                    default:
                        logger.warn("Unrecognized Name ID Format : " + fmt);
                        return new DynamicAccountLinkImpl( subject,
                                subjectNameID.getName(),
                                NameIDFormat.UNSPECIFIED.getValue(),
                                ctx instanceof Map ? (Map<String, String>) ctx : null);

                }
            } else {
                // If no format is specified, take the subject id as is, and force UNSPECIFIED
                return new DynamicAccountLinkImpl( subject,
                        subjectNameID.getName(),
                        NameIDFormat.UNSPECIFIED.getValue(),
                        ctx instanceof Map ? (Map<String, String>) ctx : null);
            }

        }

        logger.error( "Cannot create account link for subject : " + subject );

        return null;

    }

}
