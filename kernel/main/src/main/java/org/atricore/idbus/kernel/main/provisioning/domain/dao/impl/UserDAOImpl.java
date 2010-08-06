package org.atricore.idbus.kernel.main.provisioning.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserNotFoundException;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class UserDAOImpl {

    private static final Log logger = LogFactory.getLog(UserDAOImpl.class);

    private PersistenceManagerFactory pmf;
    private PersistenceManager pm;

    public void init() {
        pm = pmf.getPersistenceManager();
    }

    public void destroy() {
        if (pm != null) {
            try {
                pm.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public PersistenceManagerFactory getPmf() {
        return pmf;
    }

    public void setPmf(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }

    // User CRUD Operations

    public User create(String userName, String firstName, String surename, String commonName,
                           String givenName, String initials, String generationQualifier, String distinguishedName,
                           String email, String telephoneNumber, String facsimilTelephoneNumber, String countryName,
                           String localityName, String stateOrProvinceName, String streetAddress, String organizationName,
                           String organizationUnitName, String personalTitle, String businessCategory, String postalAddress,
                           String postalCode, String postOfficeBox, String language, boolean accountDisabled,
                           boolean accountExpires, java.util.Date accountExpirationDate, boolean limitSimultaneousLogin,
                           int maximunLogins, boolean terminatePreviousSession, boolean preventNewSession,
                           boolean allowUserToChangePassword, boolean forcePeriodicPasswordChanges, int daysBetweenChanges,
                           java.util.Date passwordExpirationDate, boolean notifyPasswordExpiration, int daysBeforeExpiration,
                           String password, byte[] userCertificate, boolean automaticallyGeneratePassword,
                           boolean emailNewPasword, Group[] groups
    ) throws ProvisioningException {

        try {
            pm.getFetchPlan().addGroup("User_f_User");

            User newUser = new User();

            newUser.setUserName(userName);
            newUser.setFirstName(firstName);
            newUser.setSurename(surename);
            newUser.setCommonName(commonName);
            newUser.setGivenName(givenName);
            newUser.setInitials(initials);
            newUser.setGenerationQualifier(generationQualifier);
            newUser.setDistinguishedName(distinguishedName);
            newUser.setEmail(email);
            newUser.setTelephoneNumber(telephoneNumber);
            newUser.setFacsimilTelephoneNumber(facsimilTelephoneNumber);
            newUser.setCountryName(countryName);
            newUser.setLocalityName(localityName);
            newUser.setStateOrProvinceName(stateOrProvinceName);
            newUser.setStreetAddress(streetAddress);
            newUser.setOrganizationName(organizationName);
            newUser.setOrganizationUnitName(organizationUnitName);
            newUser.setPersonalTitle(personalTitle);
            newUser.setBusinessCategory(businessCategory);
            newUser.setPostalAddress(postalAddress);
            newUser.setPostalCode(postalCode);
            newUser.setPostOfficeBox(postOfficeBox);
            newUser.setLanguage(language);

            newUser.setAccountDisabled(accountDisabled);
            newUser.setAccountExpires(accountExpires);
            newUser.setAccountExpirationDate(accountExpirationDate);
            newUser.setLimitSimultaneousLogin(limitSimultaneousLogin);
            newUser.setMaximunLogins(maximunLogins);
            newUser.setTerminatePreviousSession(terminatePreviousSession);
            newUser.setPreventNewSession(preventNewSession);
            newUser.setAllowUserToChangePassword(allowUserToChangePassword);
            newUser.setForcePeriodicPasswordChanges(forcePeriodicPasswordChanges);
            newUser.setDaysBetweenChanges(daysBetweenChanges);
            newUser.setPasswordExpirationDate(passwordExpirationDate);
            newUser.setNotifyPasswordExpiration(notifyPasswordExpiration);
            newUser.setDaysBeforeExpiration(daysBeforeExpiration);
            newUser.setUserPassword(password);
            newUser.setUserCertificate(userCertificate);
            newUser.setAutomaticallyGeneratePassword(automaticallyGeneratePassword);
            newUser.setEmailNewPasword(emailNewPasword);

            if (groups != null) {

                Group[] newGroups = new Group[groups.length];

                for (int i = 0; i < groups.length; i++) {

                    // TODO : User GroupDAO
                    Query query = pm.newQuery(Group.class, "id == :id");
                    Collection result = (Collection) query.execute(groups[i].getId());
                    Iterator it = result.iterator();
                    if (!it.hasNext()) {
                        throw new GroupNotFoundException(groups[i].getId());
                    }

                    newGroups[i] = (Group) it.next();
                }

                newUser.setGroups(newGroups);
            }
            if (logger.isTraceEnabled())
                logger.trace("Create User : '" + userName + "'");

            return pm.makePersistent(newUser);

        } catch (Exception e) {
            throw new ProvisioningException("Error persisting User '" + userName + "' ", e);
        }
    }

    public User retrieve(long id) throws UserNotFoundException {

        pm.getFetchPlan().addGroup("User_f_User");
        String qryStr = "id == " + id;

        if (logger.isTraceEnabled())
            logger.trace("Retrieve User. Query '" + qryStr + "'");

        Query q = pm.newQuery(User.class, qryStr);
        Collection result = (Collection) q.execute();

        if (result.isEmpty())
            throw new UserNotFoundException(id);

        Iterator iter = result.iterator();
        User user = pm.detachCopy((User) iter.next());

        if (logger.isTraceEnabled())
            logger.trace("Retrieved User " + user.getId() + " '" + user.getUserName() + "'");

        return user;

    }

    public User update(User user) throws UserNotFoundException, GroupNotFoundException {
        pm.getFetchPlan().addGroup("User_f_User");

        String qryStr = "id == " + user.getId();

        if (logger.isTraceEnabled())
            logger.trace("Update User. Query '" + qryStr + "'");

        Query q = pm.newQuery(User.class, qryStr);
        Collection result = (Collection) q.execute();
        Iterator it = result.iterator();

        if (!it.hasNext())
            throw new UserNotFoundException(user.getId(), user.getUserName());

        User oldUser = (User) it.next();

        oldUser.setUserName(user.getUserName());
        oldUser.setFirstName(user.getFirstName());
        oldUser.setSurename(user.getSurename());
        oldUser.setCommonName(user.getCommonName());
        oldUser.setGivenName(user.getGivenName());
        oldUser.setInitials(user.getInitials());
        oldUser.setGenerationQualifier(user.getGenerationQualifier());
        oldUser.setDistinguishedName(user.getDistinguishedName());
        oldUser.setEmail(user.getEmail());
        oldUser.setTelephoneNumber(user.getTelephoneNumber());
        oldUser.setFacsimilTelephoneNumber(user.getFacsimilTelephoneNumber());
        oldUser.setCountryName(user.getCountryName());
        oldUser.setLocalityName(user.getLocalityName());
        oldUser.setStateOrProvinceName(user.getStateOrProvinceName());
        oldUser.setStreetAddress(user.getStreetAddress());
        oldUser.setOrganizationName(user.getOrganizationName());
        oldUser.setOrganizationUnitName(user.getOrganizationUnitName());
        oldUser.setPersonalTitle(user.getPersonalTitle());
        oldUser.setBusinessCategory(user.getBusinessCategory());
        oldUser.setPostalAddress(user.getPostalAddress());
        oldUser.setPostalCode(user.getPostalCode());
        oldUser.setPostOfficeBox(user.getPostOfficeBox());
        oldUser.setLanguage(user.getLanguage());
        oldUser.setAccountDisabled(user.getAccountDisabled());
        oldUser.setAccountExpires(user.getAccountExpires());
        oldUser.setAccountExpirationDate(user.getAccountExpirationDate());
        oldUser.setLimitSimultaneousLogin(user.getLimitSimultaneousLogin());
        oldUser.setMaximunLogins(user.getMaximunLogins());
        oldUser.setTerminatePreviousSession(user.getTerminatePreviousSession());
        oldUser.setPreventNewSession(user.getPreventNewSession());
        oldUser.setAllowUserToChangePassword(user.getAllowUserToChangePassword());
        oldUser.setForcePeriodicPasswordChanges(user.getForcePeriodicPasswordChanges());
        oldUser.setDaysBetweenChanges(user.getDaysBetweenChanges());
        oldUser.setPasswordExpirationDate(user.getPasswordExpirationDate());
        oldUser.setNotifyPasswordExpiration(user.getNotifyPasswordExpiration());
        oldUser.setDaysBeforeExpiration(user.getDaysBeforeExpiration());

        if (user.getUserPassword() != null && !user.getUserPassword().equals(""))
            oldUser.setUserPassword(user.getUserPassword());

        oldUser.setUserCertificate(user.getUserCertificate());
        oldUser.setAutomaticallyGeneratePassword(user.getAutomaticallyGeneratePassword());
        oldUser.setEmailNewPasword(user.getEmailNewPasword());

        if (user.getGroups() != null) {
            Group[] groups = new Group[user.getGroups().length];

            for (int i = 0; i < user.getGroups().length; i++) {

                Query query = pm.newQuery(Group.class, "id == :id");
                result = (Collection) query.execute(user.getGroups()[i].getId());
                it = result.iterator();

                if (!it.hasNext()) {
                    throw new GroupNotFoundException(user.getGroups()[i].getId());
                }

                groups[i] = (Group) it.next();
            }

            oldUser.setGroups(groups);
        }
        pm.detachCopy(oldUser);

        return oldUser;


    }

    public void delete(long id) throws UserNotFoundException {

        String qryStr = "id == " + id;

        if (logger.isTraceEnabled())
            logger.trace("Delete User " + id);

        Query q = pm.newQuery(User.class, qryStr);
        Collection result = (Collection) q.execute();
        Iterator it = result.iterator();

        if (!it.hasNext())
            throw new UserNotFoundException(id);

        pm.deletePersistent(it.next());


    }

    // More retrieve operations 

    public Collection<User> retrieveAll() {
        return null;
    }


}
