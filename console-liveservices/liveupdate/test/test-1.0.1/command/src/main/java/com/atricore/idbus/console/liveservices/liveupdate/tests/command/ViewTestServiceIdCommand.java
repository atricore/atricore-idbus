package com.atricore.idbus.console.liveservices.liveupdate.tests.command;

import com.atricore.idbus.console.liveservices.liveupdate.tests.services.spi.DistributableTestService;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "liveupdate-dev", name = "test-svc-id", description = "View configured profile details")
public class ViewTestServiceIdCommand extends LiveUpdateTestsCommandSupport {

    @Override
    protected Object doExecute(DistributableTestService svc) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("Test Service (DistributableService) ID  : ");
        sb.append(svc.getGroupId());
        sb.append("/");
        sb.append(svc.getArtifactId());
        sb.append("/");
        sb.append(svc.getVersion());

        System.out.println(sb.toString());
        return null;
    }
}
