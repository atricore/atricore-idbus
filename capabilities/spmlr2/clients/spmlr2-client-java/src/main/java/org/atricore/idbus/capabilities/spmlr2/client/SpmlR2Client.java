package org.atricore.idbus.capabilities.spmlr2.client;

import oasis.names.tc.spml._2._0.ExtensibleType;
import oasis.names.tc.spml._2._0.PSOType;
import oasis.names.tc.spml._2._0.SelectionType;
import oasis.names.tc.spml._2._0.password.ResetPasswordRequestType;
import oasis.names.tc.spml._2._0.password.ResetPasswordResponseType;
import oasis.names.tc.spml._2._0.password.VerifyResetPasswordRequestType;
import oasis.names.tc.spml._2._0.password.VerifyResetPasswordResponseType;
import oasis.names.tc.spml._2._0.search.ScopeType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import oasis.names.tc.spml._2._0.wsdl.SPMLRequestPortType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class SpmlR2Client implements ConfigurationConstants {

    private static final Log logger = LogFactory.getLog(SpmlR2Client.class);

    protected Properties config;

    protected String configPath;

    private boolean init;

    private String targetId;

    private String clientId;

    private String clientSecret;

    private String endpoint;

    private String wsdlLocation;

    private SPMLRequestPortType wsClient;

    public SpmlR2Client(Properties config) {
        this.config = config;
    }

    public SpmlR2Client(String configPath) {
        this.configPath = configPath;
    }


    /**
     * Initializes the client by loading the configuration if necessary.
     *
     * @throws SpmlR2ClientException if an error occurs while loading the configuration.
     */
    public void init() throws SpmlR2ClientException {

        try {

            if (config == null)
                config = loadConfig();

            this.clientId = config.getProperty(CLIENT_ID);
            this.clientSecret = config.getProperty(CLIENT_SECRET);
            this.endpoint = config.getProperty(SPML_ENDPOINT);
            this.targetId = config.getProperty(TARGET_ID);
            this.wsdlLocation = config.getProperty(WSDL_LOCATION);

            logger.info("Initialized SPML2 Clinet [" + clientId + "] for target/endpoint ["+targetId  + "|" + endpoint + "]");

            this.wsClient = doMakeWsClient();

            init = true;
        } catch (IOException e) {
            throw new SpmlR2ClientException(e);
        }
    }

    /**
     * Depends on IDM settings, the response will contain a transaction ID or verification code,
     * or a new password value.
     */
    public ResetPasswordResponseType prepareResetPassword(String username) throws SpmlR2ClientException {

        PSOType user = searchUserPSO(username);

        // TODO : Request OAuth2 token
        ResetPasswordRequestType req = new ResetPasswordRequestType();
        req.setPsoID(user.getPsoID());
        req.setRequestID(newUUID());

        ResetPasswordResponseType res = wsClient.spmlResetPasswordRequest(req);

        return res;
    }

    public void verifyResetPassword(String username, String transactionId, String newpassword) throws SpmlR2ClientException {

        PSOType user = searchUserPSO(username);
        if (user ==null)
            throw new SpmlR2ClientException("User not found " + username);

        VerifyResetPasswordRequestType req = new VerifyResetPasswordRequestType();
        req.setPsoID(user.getPsoID());
        req.setNewpassword(newpassword);
        req.setTransaction(transactionId);

        VerifyResetPasswordResponseType resp = wsClient.spmlVerifyResetPasswordRequest(req);

    }

    public ExtensibleType searchUser(String username) throws SpmlR2ClientException {
        PSOType pso = searchUserPSO(username);

        if (pso != null)
            return pso.getData();

        return null;
    }

    public PSOType searchUserPSO(String username) throws SpmlR2ClientException {

        SearchRequestType spmlRequest = new SearchRequestType();
        spmlRequest.setRequestID(newUUID());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(targetId);

        spmlRequest.setQuery(spmlQry);

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        String qry = "/users[userName='"+username+"']";

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName( SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);

        SearchResponseType spmlResponse = wsClient.spmlSearchRequest(spmlRequest);

        List<PSOType> users = spmlResponse.getPso();

        if (users.size() == 0)
            return null;

        if (users.size() > 1)
            throw new SpmlR2ClientException("Too many users found for " + username);

        PSOType pso = users.get(0);

        return pso;
    }

    protected String newUUID() {
        return "id-" + java.util.UUID.randomUUID().toString();
    }

    protected SPMLRequestPortType doMakeWsClient() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(SPMLRequestPortType.class);
        factory.setAddress(endpoint);
        if (wsdlLocation != null && !wsdlLocation.equals(""))
            factory.setWsdlLocation(wsdlLocation);

        SPMLRequestPortType client = (SPMLRequestPortType) factory.create();

        return client;

    }

    protected Properties loadConfig() throws IOException, SpmlR2ClientException {

        if (configPath == null)
            configPath = "/spml2.properties";

        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream(configPath);
        if (is == null)
            throw new SpmlR2ClientException("Configuration not found for " + configPath);

        props.load(is);
        return props;
    }
}
