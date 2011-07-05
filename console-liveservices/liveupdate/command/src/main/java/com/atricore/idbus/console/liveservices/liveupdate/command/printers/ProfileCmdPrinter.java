package com.atricore.idbus.console.liveservices.liveupdate.command.printers;


import com.atricore.idbus.console.liveservices.liveupdate.command.LiveUpdateCommandSupport;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.RequiredFeatureType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ProfileCmdPrinter extends AbstractCmdPrinter<ProfileType> {

    public void print(LiveUpdateCommandSupport cmd, ProfileType p) {

        StringBuilder sb = new StringBuilder();
        // Build headers line

        sb.append("Profile\n");
        sb.append(getNameValue("ID", p.getID()));
        sb.append(getNameValue("Name", p.getName()));
        sb.append(getNameValue("Installable Units", (p.getInstallableUnit() != null ? p.getInstallableUnit().size() + "" : "<null>")));
        sb.append("\n");

        for (InstallableUnitType iu : p.getInstallableUnit()) {

            sb.append("Installable Unit\n");

            sb.append(getNameValue("ID", iu.getID()));
            sb.append(getNameValue("Group", iu.getGroup()));
            sb.append(getNameValue("Name", iu.getName()));
            sb.append(getNameValue("Version", iu.getVersion()));
            sb.append(getNameValue("Nature", iu.getUpdateNature().value()));


            if (cmd.isVerbose()) {

                int i = 0;
                for (ArtifactKeyType art : iu.getArtifact()) {
                    sb.append(getNameValue("Artifact ("+i+")",
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

        }

        getOut().println(sb);

    }

}
