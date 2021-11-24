/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.kernel.main.federation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.BaseRole;
import org.atricore.idbus.kernel.main.authn.BaseUser;
import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.store.SimpleUserKey;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;

//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
import javax.security.auth.Subject;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Account Link Lifecycle Manager
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: ResourceCircleOfTrustMemberDescriptorImpl.java 1269 2009-06-11 16:28:55Z sgonzalez $
 */
//@Repository
// TODO : Implement persistence support @Transactional
public class AccountLinkLifecycleImpl implements AccountLinkLifecycle {

    private static final Log logger = LogFactory.getLog(AccountLinkLifecycleImpl.class);

    private IdentityStore identityStore;

	//private EntityManager entityManager;

    public PersistentAccountLink establishPersistent(Subject idpSubject, String localSubjectNameIdentifier) {
        throw new UnsupportedOperationException("Unsupported Account Link Lifecycle Operation");
        /*
        PersistentAccountLink pal = new PersistentAccountLinkImpl(idpSubject,
                localSubjectNameIdentifier,
                "PERSISTENT");
        entityManager.persist(pal);
        return pal;
        */
    }



    public TransientAccountLink establishTransient(Subject idpSubject, String localSubjectNameIdentifier) {
        throw new UnsupportedOperationException("Unsupported Account Link Lifecycle Operation");
    }

    public boolean persistentForIDPSubjectExists(Subject idpSubject) {
    	boolean exists = false;
    	if (findByIDPAccount(idpSubject) != null) exists = true;
    	return exists;
    }

    public boolean transientForIDPSubjectExists(Subject idpSubject) {
        return false;
    }

    public AccountLink findByIDPAccount(Subject idpSubject) {
        throw new UnsupportedClassVersionError("Unsupported Account Link Lifecycle Operation");
        /*
    	AccountLink accountLink = null;
    	SubjectNameID subjectNameID = null;
    	for(Principal absPrincipal : idpSubject.getPrincipals()){
    		if(absPrincipal instanceof SubjectNameID){
    			subjectNameID = (SubjectNameID)absPrincipal;
    		}
    	}
    	if(subjectNameID != null){
    		List result = entityManager.createQuery("select distinct pal from PersistentAccountLinkImpl pal inner join pal.idpSubject.principals pr " +
      			"where pr IN (from SubjectNameID snID where snID.name=:name) and pal.deleted=:del")
      			.setParameter("name", subjectNameID.getName()).setParameter("del", new Boolean(false)).getResultList();

	        if(result != null && result.size() != 0){
	        	accountLink = (AccountLink)result.get(0);
	        }
    	}
        return accountLink;
        */
    }

    public AccountLink findByLocalAccount(Subject localSubject) {
        throw new UnsupportedClassVersionError("Unsupported Account Link Lifecycle Operation");
        /*
    	AccountLink accountLink = null;
    	SubjectNameID subjectNameID = null;
    	for(Principal absPrincipal : localSubject.getPrincipals()){
    		if(absPrincipal instanceof SubjectNameID){
    			subjectNameID = (SubjectNameID)absPrincipal;
    		}
    	}
    	if(subjectNameID != null){
    		List result = entityManager.createQuery("select distinct pal from PersistentAccountLinkImpl pal inner join pal.idpSubject.principals pr " +
      			"where pr IN (from SubjectNameID snID where snID.localName=:name) and pal.deleted=:del")
      			.setParameter("name", subjectNameID.getName()).setParameter("del", new Boolean(false)).getResultList();

	        if(result != null && result.size() != 0){
	        	accountLink = (AccountLink)result.get(0);
	        }
    	}
        return accountLink;
        */
    }

    public Subject resolve(AccountLink accountLink) throws AccountLinkageException {


        Subject resolvedSubject = new Subject();

        if (identityStore == null) {

            String userId = accountLink.getLocalAccountNameIdentifier() != null ?
                    accountLink.getLocalAccountNameIdentifier() :
                    accountLink.getId();

            // TODO : What type of username are we using here?
            resolvedSubject.getPrincipals().add(new SubjectNameID(userId, null));

            if (logger.isDebugEnabled())
                logger.debug("No local identity store, returning local subject as " + resolvedSubject);

            return resolvedSubject;
        }

        UserKey uid = accountLink.getUserKey();

        try {
            logger.debug("Resolving account link : " + accountLink.getLocalAccountNameIdentifier());

            if (identityStore.userExists(uid)) {

                BaseUser user = identityStore.loadUser(uid);

                // map it to josso2 subject data model
                resolvedSubject.getPrincipals().add(
                        // TODO : What type of username are we using here?
                        new SubjectNameID(user.getName(), null)
                );

                SSONameValuePair[] ssoUserProperties = user.getProperties();

                for ( SSONameValuePair ssoUserProperty : ssoUserProperties ) {
                    resolvedSubject.getPrincipals().add(
                            new SubjectAttribute(ssoUserProperty.getName(), ssoUserProperty.getValue())
                    );
                }

                BaseRole[] roles = identityStore.findRolesByUserKey(uid);

                for (BaseRole role : roles) {
                    resolvedSubject.getPrincipals().add(
                            new SubjectRole(role.getName())
                    );
                }
            } else {
                logger.warn("User ["+uid+"] does not exists in Identity Store (" + identityStore + ") ! Cannot resolve account link " + accountLink.getId());
            }

        } catch (SSOIdentityException e) {
            throw new AccountLinkageException("Error resolving account link [" + accountLink.getId() + "] " + e.getMessage(), e);
        }

        return resolvedSubject;
    }

    public AccountLink disable(AccountLink accountLink) {


    	if(accountLink instanceof PersistentAccountLinkImpl){
            throw new UnsupportedClassVersionError("Unsupported Account Link Lifecycle Operation");
            /*
    		//we want to update only 'enabled' property
    		accountLink = entityManager.find(PersistentAccountLinkImpl.class, ((PersistentAccountLinkImpl)accountLink).getPersistanceId());
    		if(accountLink != null){
    			accountLink.setEnabled(false);
    			accountLink = entityManager.merge(accountLink);
    		} */
    	}
    	return accountLink;


    }

    public AccountLink enable(AccountLink accountLink) {

    	if(accountLink instanceof PersistentAccountLinkImpl){
            throw new UnsupportedClassVersionError("Unsupported Account Link Lifecycle Operation");
            /*
    		//we want to update only 'enabled' property
    		accountLink = entityManager.find(PersistentAccountLinkImpl.class, ((PersistentAccountLinkImpl)accountLink).getPersistanceId());
    		if(accountLink != null){
		    	accountLink.setEnabled(true);
		    	accountLink = entityManager.merge(accountLink);
    		}
    		*/
    	}
    	return accountLink;
    }

    public AccountLink dispose(AccountLink accountLink) {
    	if(accountLink instanceof PersistentAccountLinkImpl){
            throw new UnsupportedClassVersionError("Unsupported Account Link Lifecycle Operation");
            /*
    		accountLink = entityManager.find(PersistentAccountLinkImpl.class, ((PersistentAccountLinkImpl)accountLink).getPersistanceId());
    		if(accountLink != null){
		    	accountLink.setDeleted(true);
		    	accountLink = entityManager.merge(accountLink);
    		}
    		*/
    	}
    	return accountLink;
    }

    /**
     *
     * @org.apache.xbean.Property alias="identity-store"
     */
    public IdentityStore getIdentityStore() {
        return identityStore;
    }

    public void setIdentityStore(IdentityStore identityStore) {
        this.identityStore = identityStore;
    }

    /*
    @PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	} */


}
