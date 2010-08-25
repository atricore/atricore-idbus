package org.atricore.idbus.capabilities.spmlr2.main.test;

import oasis.names.tc.spml._2._0.SelectionType;
import oasis.names.tc.spml._2._0.search.ScopeType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.util.XmlUtils;
import org.junit.Test;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */

public class TestXmlUtils {

    @Test
    public void testMarshallSearchRequest() throws Exception {
        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID("111111");
        searchRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID("target-id");
        String qry="";



        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        qry = "/groups[name='group1']";

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName( SPMLR2Constants.SPML_NS, "Selection"),
                spmlSelect.getClass(),
                spmlSelect
        );


        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        String marshalled = XmlUtils.marshallSpmlR2Request(searchRequest, false);

        System.out.println(marshalled);

    }
}
