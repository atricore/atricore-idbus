package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "usrmodify", description = "SPML User MODIFY operation")
public class UserModifyCommand extends SpmlCommandSupport {

    @Option(name = "-i", aliases = "--id", description = "User ID", required = true, multiValued = false)
    Long id;

    //<--- General Information ---->
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
    @Option(name = "--account-disabled", description = "Account disabled", required = false, multiValued = false)
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
    byte[] userCertificate;
    Boolean automaticallyGeneratePassword;
    Boolean emailNewPasword;

//<--- Groups Membership ---->
    @Option(name = "-g", aliases = "--group", description = "User group names", required = false, multiValued = true)
    List<String> groupName = new ArrayList<String>();

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {
        ModifyRequestType spmlRequest = new ModifyRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        PSOType psoUser = lookupUser(pspChannel, id);
        UserType spmlUser = (UserType) psoUser.getData();

        // Note: use Spring's BeanUtils (instead of Apache's) because null Boolean values will be set to null and not false
        // Note: null values will be ignored for user update
        BeanUtils.copyProperties(this, spmlUser);

        if (this.groupName != null) {

            spmlUser.getGroup().clear();

            for (String groupName : this.groupName) {
                PSOType psoGroup = lookupGroup(pspChannel, groupName);
                GroupType spmlGroup = (GroupType) psoGroup.getData();
                spmlUser.getGroup().add(spmlGroup);
            }

        }

        ModificationType mod = new ModificationType();

        mod.setModificationMode(ModificationModeType.REPLACE);
        mod.setData(spmlUser);

        spmlRequest.setPsoID(psoUser.getPsoID());
        spmlRequest.getModification().add(mod);

        return spmlRequest;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurename() {
        return surename;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getInitials() {
        return initials;
    }

    public String getGenerationQualifier() {
        return generationQualifier;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getFacsimilTelephoneNumber() {
        return facsimilTelephoneNumber;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getLocalityName() {
        return localityName;
    }

    public String getStateOrProvinceName() {
        return stateOrProvinceName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getOrganizationUnitName() {
        return organizationUnitName;
    }

    public String getPersonalTitle() {
        return personalTitle;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPostOfficeBox() {
        return postOfficeBox;
    }

    public String getLanguage() {
        return language;
    }

    public Boolean getAccountDisabled() {
        return accountDisabled;
    }

    public Boolean getAccountExpires() {
        return accountExpires;
    }

    public Date getAccountExpirationDate() {
        return accountExpirationDate;
    }

    public Boolean getLimitSimultaneousLogin() {
        return limitSimultaneousLogin;
    }

    public Integer getMaximunLogins() {
        return maximunLogins;
    }

    public Boolean getTerminatePreviousSession() {
        return terminatePreviousSession;
    }

    public Boolean getPreventNewSession() {
        return preventNewSession;
    }

    public Boolean getAllowUserToChangePassword() {
        return allowUserToChangePassword;
    }

    public Boolean getForcePeriodicPasswordChanges() {
        return forcePeriodicPasswordChanges;
    }

    public Integer getDaysBetweenChanges() {
        return daysBetweenChanges;
    }

    public Date getPasswordExpirationDate() {
        return passwordExpirationDate;
    }

    public Boolean getNotifyPasswordExpiration() {
        return notifyPasswordExpiration;
    }

    public Integer getDaysBeforeExpiration() {
        return daysBeforeExpiration;
    }

    public byte[] getUserCertificate() {
        return userCertificate;
    }

    public Boolean getAutomaticallyGeneratePassword() {
        return automaticallyGeneratePassword;
    }

    public Boolean getEmailNewPasword() {
        return emailNewPasword;
    }
}
