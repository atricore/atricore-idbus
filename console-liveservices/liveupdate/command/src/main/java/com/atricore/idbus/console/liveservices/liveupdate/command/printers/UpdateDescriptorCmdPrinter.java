package com.atricore.idbus.console.liveservices.liveupdate.command.printers;

import com.atricore.idbus.console.liveservices.liveupdate.command.LiveUpdateCommandSupport;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.RequiredFeatureType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateDescriptorCmdPrinter extends AbstractCmdPrinter<UpdateDescriptorType> {

    public void print(LiveUpdateCommandSupport cmd, UpdateDescriptorType o) {

        StringBuffer sb = new StringBuffer();

        sb.append("Update\n");

        sb.append(getNameValue("Id", o.getID()));
        sb.append(getNameValue("Description", o.getDescription()));
        sb.append(getNameValue("Issued", o.getIssueInstant().toString()));

        for (InstallableUnitType iu : o.getInstallableUnit()) {

            sb.append("Installable Unit\n");

            sb.append(getNameValue("ID", iu.getID()));
            sb.append(getNameValue("Group", iu.getGroup()));
            sb.append(getNameValue("Name", iu.getName()));
            sb.append(getNameValue("Version", iu.getVersion()));
            sb.append(getNameValue("Nature", iu.getUpdateNature().value()));

            int i = 0;
            for (ArtifactKeyType art : iu.getArtifact()) {
                sb.append(getNameValue("Artifact (" + i + ")",
                        art.getGroup() +
                                "/" + art.getName() +
                                "/" + art.getVersion() +
                                "/" + art.getName() +
                                "-" + art.getVersion() +
                                (art.getClassifier() != null ? "-" + art.getClassifier() : "") +
                                "." + (art.getType() != null ? art.getType() : "jar")));
                i++;
            }

            i = 0;
            for (RequiredFeatureType req : iu.getRequirement()) {
                sb.append(getNameValue("Requirement",
                        req.getGroup() + "/" + req.getName() + "/" + req.getVersionRange().getExpression()
                ));

                i++;
            }

        }
        sb.append("\n");

        getOut().append(sb.toString());
    }

}
