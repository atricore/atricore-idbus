package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.AddRequestType;
import oasis.names.tc.spml._2._0.PSOType;
import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "usradd", description = "SPML ADD operation")
public class UserAddCommand extends SpmlCommandSupport {

    //<--- General Information ---->
    @Option(name = "-u", aliases = "--username", description = "Username ", required = true, multiValued = false)
    String userName;
    
    @Option(name = "-n", aliases = "--name", description = "User first name ", required = false, multiValued = false)
    String firstName;

    @Option(name = "-s", aliases = "--surename", description = "User last name", required = false, multiValued = false)
    String surename;

    String commonName;
    String givenName;
    String initials;
    String generationQualifier;
    String distinguishedName;
    @Option(name = "-e", aliases = "--email", description = "User e-mail", required = false, multiValued = false)
    String email;

    String telephoneNumber;
    String facsimilTelephoneNumber;
    String countryName;
    String localityName;
    String stateOrProvinceName;
    String streetAddress;
    String organizationName;
    String organizationUnitName;
    String personalTitle;
    String businessCategory;
    String postalAddress;
    String postalCode;
    String postOfficeBox;


//<--- Preference ---->
    @Option(name = "--language", description = "User prefered langauge", required = false, multiValued = false)
    String language;


//<--- Security Account---->
    Boolean accountDisabled;
    Boolean accountExpires;
    Date accountExpirationDate;
    Boolean limitSimultaneousLogin;
    Integer maximunLogins;
    Boolean terminatePreviousSession;
    Boolean preventNewSession;

//<--- Security Password---->
    Boolean allowUserToChangePassword;
    Boolean forcePeriodicPasswordChanges;
    Integer daysBetweenChanges;
    Date passwordExpirationDate;
    Boolean notifyPasswordExpiration;
    Integer daysBeforeExpiration;

//<--- Security Set Password---->
    @Option(name = "-p", aliases = "--password", description = "User Password", required = false, multiValued = false)
    String userPassword;
    byte[] userCertificate;
    Boolean automaticallyGeneratePassword;
    Boolean emailNewPasword;

//<--- Groups Membership ---->
    @Option(name = "-g", aliases = "--group", description = "User group names", required = false, multiValued = true)
    List<String> groupName = new ArrayList<String>();

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception  {
        AddRequestType req = new AddRequestType();
        req.setRequestID(uuidGenerator.generateId());
        req.setTargetID(targetId);

        // Use Atricore SPML schema ...
        UserType spmlUser = new UserType();

        // Fill with user properties
        BeanUtils.copyProperties(spmlUser, this);

        // Recover list of Groups

        if (this.groupName != null) {
            spmlUser.getGroup().clear();
            for (String groupName : this.groupName) {
                PSOType psoGroup = lookupGroup(pspChannel, groupName);
                GroupType spmlGroup = (GroupType) psoGroup.getData();
                spmlUser.getGroup().add(spmlGroup);
            }
        }

        req.setData(spmlUser);
        req.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        return req;
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

    public Boolean getAccountDisabled() {
        return accountDisabled;
    }

    public void setAccountDisabled(Boolean accountDisabled) {
        this.accountDisabled = accountDisabled;
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

    public Boolean getTerminatePreviousSession() {
        return terminatePreviousSession;
    }

    public void setTerminatePreviousSession(Boolean terminatePreviousSession) {
        this.terminatePreviousSession = terminatePreviousSession;
    }

    public Boolean getPreventNewSession() {
        return preventNewSession;
    }

    public void setPreventNewSession(Boolean preventNewSession) {
        this.preventNewSession = preventNewSession;
    }

    public Boolean getAllowUserToChangePassword() {
        return allowUserToChangePassword;
    }

    public void setAllowUserToChangePassword(Boolean allowUserToChangePassword) {
        this.allowUserToChangePassword = allowUserToChangePassword;
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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public byte[] getUserCertificate() {
        return userCertificate;
    }

    public void setUserCertificate(byte[] userCertificate) {
        this.userCertificate = userCertificate;
    }

    public Boolean getAutomaticallyGeneratePassword() {
        return automaticallyGeneratePassword;
    }

    public void setAutomaticallyGeneratePassword(Boolean automaticallyGeneratePassword) {
        this.automaticallyGeneratePassword = automaticallyGeneratePassword;
    }

    public Boolean getEmailNewPasword() {
        return emailNewPasword;
    }

    public void setEmailNewPasword(Boolean emailNewPasword) {
        this.emailNewPasword = emailNewPasword;
    }

    public List<String> getGroupName() {
        return groupName;
    }

    public void setGroupName(List<String> groupName) {
        this.groupName = groupName;
    }
}
