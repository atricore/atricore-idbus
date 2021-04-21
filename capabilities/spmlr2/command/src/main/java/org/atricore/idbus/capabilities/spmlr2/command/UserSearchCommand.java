package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.SelectionType;
import oasis.names.tc.spml._2._0.atricore.UserSearchCriteriaType;
import oasis.names.tc.spml._2._0.atricore.UserSearchRequestType;
import oasis.names.tc.spml._2._0.search.ScopeType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "usrsearch", description = "SPML User SEARCH operation")
public class UserSearchCommand extends SpmlCommandSupport {
    
    @Option(name = "-q", aliases = "--query", description = "SPML User search query", required = false, multiValued = false)
    String qry;

    @Option(name = "-n", aliases = "--name", description = "SPML Username", required = false, multiValued = false)
    String username;


    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) {
        UserSearchRequestType spmlRequest = new UserSearchRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(targetId);

        spmlRequest.setQuery(spmlQry);

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        if (username != null)
            qry = "/users[userName='"+username+"']";

        UserSearchCriteriaType userSearchCriteria = new UserSearchCriteriaType();
        userSearchCriteria.setUserName(username);
        userSearchCriteria.setExactMatch(true);
        spmlRequest.setUserSearchCriteria(userSearchCriteria);
        spmlRequest.setFromResult(0l);
        spmlRequest.setResultCount(0l);

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName( SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);

        return spmlRequest;
    }
    
}
