package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.*;
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
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "grpmodify", description = "SPML User MODIFY operation")
public class UserModifyCommand extends SpmlCommandSupport {

    @Option(name = "-i", aliases = "--id", description = "Group ID", required = true, multiValued = false)
    Long id;

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
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {
        ModifyRequestType spmlRequest = new ModifyRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        PSOType psoUser = lookupUser(pspChannel, id);
        UserType spmlUser = (UserType) psoUser.getData();

        BeanUtils.copyProperties(spmlUser, this);

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

}
