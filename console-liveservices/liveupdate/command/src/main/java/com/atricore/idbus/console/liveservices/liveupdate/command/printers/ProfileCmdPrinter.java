package com.atricore.idbus.console.liveservices.liveupdate.command.printers;


import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ProfileCmdPrinter extends AbstractCmdPrinter<ProfileType> {

    public void print(ProfileType p) {

        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("\u001B[1m Profile           : [").append(getNameString(p.getID(), 16)).append("] ").append(p.getName()).append("\u001B[0m ");
        sb.append(" (IUs=").append(p.getInstallableUnit().size()).append(")\n") ;

        for (InstallableUnitType iu : p.getInstallableUnit()) {
            sb.append("  Installable Unit : [");
            sb.append(getNameString(iu.getID(), 16));
            sb.append("] ");
            sb.append(iu.getGroup());
            sb.append("/");
            sb.append(iu.getName());
            sb.append("/");
            sb.append(getNameString(iu.getVersion(), 8));
            sb.append(" (");
            sb.append(getNameString(iu.getUpdateNature().toString(), 12));
            sb.append(")");
            sb.append("\n");

            for (ArtifactKeyType art : iu.getArtifact()) {
                sb.append("    Artifact       : [");
                sb.append(getNameString(art.getID(), 16));
                sb.append("] ");
                sb.append(art.getGroup());
                sb.append("/");
                sb.append(art.getName());
                sb.append("/");
                sb.append(art.getVersion());
                sb.append("/");
                sb.append(art.getClassifier());
                sb.append("\n");
            }

        }

        getOut().println(sb);

    }

}
