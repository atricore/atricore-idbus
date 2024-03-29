package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOUser implements Serializable {

    private static final long serialVersionUID = -2547786148798290707L;

    private Long id;

//<--- General Information ---->
    private String userName;
    private String firstName;
    private String surename;
    private String commonName;
    private String givenName;
    private String initials;
    private String generationQualifier;
    private String distinguishedName;
    private String email;
    private String telephoneNumber;
    private String facsimilTelephoneNumber;
    private String countryName;
    private String localityName;
    private String stateOrProvinceName;
    private String streetAddress;
    private String organizationName;
    private String organizationUnitName;
    private String personalTitle;
    private String businessCategory;
    private String postalAddress;
    private String postalCode;
    private String postOfficeBox;


//<--- Preference ---->
    private String language;

//<--- Groups Membership ---->
    private JDOGroup[] groups;

//<--- Life Questions Membership ---->
    private JDOUserSecurityQuestion[] securityQuestions;


//<--- Security Account---->
    private Boolean accountDisabled;
    private Boolean accountExpires;
    private Date accountExpirationDate;
    private Boolean limitSimultaneousLogin;
    private Integer maximunLogins;
    private Boolean terminatePreviousSession;
    private Boolean preventNewSession;
    private Date lastAuthentication;
    private Date accountCreationDate;
    private Date accountModificationDate;
    private Integer failedLogins;

//<--- Security Password---->
    private Boolean allowUserToChangePassword;
    private Boolean forcePeriodicPasswordChanges;
    private Integer daysBetweenChanges;
    private Date passwordExpirationDate;
    private Boolean notifyPasswordExpiration;
    private Integer daysBeforeExpiration;
    private Date lastPasswordChangeDate;

//<--- Security Set Password---->
    private String userPassword;
    private String userCertificate;
    private Boolean automaticallyGeneratePassword;
    private Boolean emailNewPasword;
    private String salt;

    //<--- Access Control ---->
    private JDOAcl[] acls;

//<--- Extended Attributes ---->
    private JDOUserAttributeValue[] attrs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurename() {
        return surename;
    }

    public void setSurename(String surename) {
        this.surename = surename;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getGenerationQualifier() {
        return generationQualifier;
    }

    public void setGenerationQualifier(String generationQualifier) {
        this.generationQualifier = generationQualifier;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getFacsimilTelephoneNumber() {
        return facsimilTelephoneNumber;
    }

    public void setFacsimilTelephoneNumber(String facsimilTelephoneNumber) {
        this.facsimilTelephoneNumber = facsimilTelephoneNumber;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getStateOrProvinceName() {
        return stateOrProvinceName;
    }

    public void setStateOrProvinceName(String stateOrProvinceName) {
        this.stateOrProvinceName = stateOrProvinceName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationUnitName() {
        return organizationUnitName;
    }

    public void setOrganizationUnitName(String organizationUnitName) {
        this.organizationUnitName = organizationUnitName;
    }

    public String getPersonalTitle() {
        return personalTitle;
    }

    public void setPersonalTitle(String personalTitle) {
        this.personalTitle = personalTitle;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostOfficeBox() {
        return postOfficeBox;
    }

    public void setPostOfficeBox(String postOfficeBox) {
        this.postOfficeBox = postOfficeBox;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public JDOGroup[] getGroups() {
        return groups;
    }

    public void setGroups(JDOGroup[] groups) {
        this.groups = groups;
    }

    public JDOUserSecurityQuestion[] getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(JDOUserSecurityQuestion[] securityQuestions) {
        this.securityQuestions = securityQuestions;
    }

    public JDOUserAttributeValue[] getAttrs() {
        return attrs;
    }

    public void setAttrs(JDOUserAttributeValue[] attrs) {
        this.attrs = attrs;
    }

    public Boolean isAccountDisabled() {
        return accountDisabled;
    }

    public Boolean getAccountDisabled() {
        return accountDisabled;
    }

    public void setAccountDisabled(Boolean accountDisabled) {
        this.accountDisabled = accountDisabled;
    }

    public Boolean isAccountExpires() {
        return accountExpires;
    }

    public Boolean getAccountExpires() {
        return accountExpires;
    }

    public void setAccountExpires(Boolean accountExpires) {
        this.accountExpires = accountExpires;
    }

    public Date getAccountExpirationDate() {
        return accountExpirationDate;
    }

    public void setAccountExpirationDate(Date accountExpirationDate) {
        this.accountExpirationDate = accountExpirationDate;
    }

    public Date getLastAuthentication() {
        return lastAuthentication;
    }

    public void setLastAuthentication(Date lastAuthentication) {
        this.lastAuthentication = lastAuthentication;
    }

    public Date getAccountCreationDate() {
        return accountCreationDate;
    }

    public void setAccountCreationDate(Date accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    public Date getAccountModificationDate() {
        return accountModificationDate;
    }

    public void setAccountModificationDate(Date accountModificationDate) {
        this.accountModificationDate = accountModificationDate;
    }

    public Integer getFailedLogins() {
        return failedLogins;
    }

    public void setFailedLogins(Integer failedLogins) {
        this.failedLogins = failedLogins;
    }

    public Boolean isLimitSimultaneousLogin() {
        return limitSimultaneousLogin;
    }

    public Boolean getLimitSimultaneousLogin() {
        return limitSimultaneousLogin;
    }

    public void setLimitSimultaneousLogin(Boolean limitSimultaneousLogin) {
        this.limitSimultaneousLogin = limitSimultaneousLogin;
    }

    public Integer getMaximunLogins() {
        return maximunLogins;
    }

    public void setMaximunLogins(Integer maximunLogins) {
        this.maximunLogins = maximunLogins;
    }

    public Boolean isTerminatePreviousSession() {
        return terminatePreviousSession;
    }

    public Boolean getTerminatePreviousSession() {
        return terminatePreviousSession;
    }

    public void setTerminatePreviousSession(Boolean terminatePreviousSession) {
        this.terminatePreviousSession = terminatePreviousSession;
    }

    public Boolean isPreventNewSession() {
        return preventNewSession;
    }

    public Boolean getPreventNewSession() {
        return preventNewSession;
    }

    public void setPreventNewSession(Boolean preventNewSession) {
        this.preventNewSession = preventNewSession;
    }

    public Boolean isAllowUserToChangePassword() {
        return allowUserToChangePassword;
    }

    public Boolean getAllowUserToChangePassword() {
        return allowUserToChangePassword;
    }

    public void setAllowUserToChangePassword(Boolean allowUserToChangePassword) {
        this.allowUserToChangePassword = allowUserToChangePassword;
    }

    public Boolean isForcePeriodicPasswordChanges() {
        return forcePeriodicPasswordChanges;
    }

    public Boolean getForcePeriodicPasswordChanges() {
        return forcePeriodicPasswordChanges;
    }

    public void setForcePeriodicPasswordChanges(Boolean forcePeriodicPasswordChanges) {
        this.forcePeriodicPasswordChanges = forcePeriodicPasswordChanges;
    }

    public Integer getDaysBetweenChanges() {
        return daysBetweenChanges;
    }

    public void setDaysBetweenChanges(Integer daysBetweenChanges) {
        this.daysBetweenChanges = daysBetweenChanges;
    }

    public Date getPasswordExpirationDate() {
        return passwordExpirationDate;
    }

    public void setPasswordExpirationDate(Date passwordExpirationDate) {
        this.passwordExpirationDate = passwordExpirationDate;
    }

    public Boolean isNotifyPasswordExpiration() {
        return notifyPasswordExpiration;
    }

    public Boolean getNotifyPasswordExpiration() {
        return notifyPasswordExpiration;
    }

    public void setNotifyPasswordExpiration(Boolean notifyPasswordExpiration) {
        this.notifyPasswordExpiration = notifyPasswordExpiration;
    }

    public Integer getDaysBeforeExpiration() {
        return daysBeforeExpiration;
    }

    public void setDaysBeforeExpiration(Integer daysBeforeExpiration) {
        this.daysBeforeExpiration = daysBeforeExpiration;
    }

    public Date getLastPasswordChangeDate() {
        return lastPasswordChangeDate;
    }

    public void setLastPasswordChangeDate(Date lastPasswordChangeDate) {
        this.lastPasswordChangeDate = lastPasswordChangeDate;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUserCertificate() {
        return userCertificate;
    }

    public void setUserCertificate(String userCertificate) {
        this.userCertificate = userCertificate;
    }

    public Boolean isAutomaticallyGeneratePassword() {
        return automaticallyGeneratePassword;
    }

    public Boolean getAutomaticallyGeneratePassword() {
        return automaticallyGeneratePassword;
    }

    public void setAutomaticallyGeneratePassword(Boolean automaticallyGeneratePassword) {
        this.automaticallyGeneratePassword = automaticallyGeneratePassword;
    }

    public Boolean isEmailNewPasword() {
        return emailNewPasword;
    }

    public Boolean getEmailNewPasword() {
        return emailNewPasword;
    }

    public void setEmailNewPasword(Boolean emailNewPasword) {
        this.emailNewPasword = emailNewPasword;
    }

    public JDOAcl[] getAcls() {
        return acls;
    }

    public void setAcls(JDOAcl[] acls) {
        this.acls = acls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDOUser)) return false;

        JDOUser that = (JDOUser) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
