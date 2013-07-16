package org.atricore.idbus.idojos.virtualidentitystore.scripting;

import org.atricore.idbus.idojos.virtualidentitystore.CredentialMappingRule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @org.apache.xbean.XBean element="credential-mapping-script"
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: ScriptingCredentialMappingRule.java 1644 2010-07-27 19:31:39Z sgonzalez $
 */
public class ScriptingCredentialMappingRule extends BaseScriptingMappingRule implements CredentialMappingRule {
    private static final Log logger = LogFactory.getLog(ScriptingCredentialMappingRule.class);

    public Collection<Credential> select(Collection<Credential> sourceCredentials) {

        if (selectRule != null) {
            Collection<ScriptingRuleParameter> selectParams = new ArrayList<ScriptingRuleParameter>();

            selectParams.add(new ScriptingRuleParameterImpl( "sourceCredentials", sourceCredentials, Collection.class ));

            ScriptingRuleEngine sre = new ScriptingRuleEngine();
            ScriptingRuleExecutionOutcome outcome;

            try {
                outcome = sre.execute(selectRule, selectParams);
                return (Collection<Credential>)outcome.getObject();
            } catch (Exception e) {
                logger.error("Credential Mapping Rule Failed...ignoring", e);
            }
        }

        return sourceCredentials;
    }

    public Collection<Credential> join(Collection<Credential> selectedCredentials) {

        if (joinRule != null) {
            Collection<ScriptingRuleParameter> joinParams = new ArrayList<ScriptingRuleParameter>();

            joinParams.add(new ScriptingRuleParameterImpl( "selectedCredentials", selectedCredentials, Collection.class ));

            ScriptingRuleEngine sre = new ScriptingRuleEngine();
            ScriptingRuleExecutionOutcome outcome;

            try {
                outcome = sre.execute(joinRule, joinParams);
                return (Collection<Credential>)outcome.getObject();
            } catch (Exception e) {
                logger.error("Credential Mapping Rule Failed...ignoring", e);
            }

        }

        return null;
    }

    public Collection<Credential> transform(Collection<Credential> jointCredential) {

        if (transformRule != null) {

            Collection<ScriptingRuleParameter> transformParams = new ArrayList<ScriptingRuleParameter>();

            transformParams.add(new ScriptingRuleParameterImpl( "jointCredential", jointCredential, Credential.class ));

            ScriptingRuleEngine sre = new ScriptingRuleEngine();
            ScriptingRuleExecutionOutcome outcome;

            try {
                outcome = sre.execute(transformRule, transformParams);
                return (Collection<Credential>)outcome.getObject();
            } catch (Exception e) {
                logger.error("Credential Mapping Rule Failed...ignoring", e);
            }
        }


        return null;
    }

    public void validate(Collection<Credential> transformedCredentials) {

        if (validateRule != null) {
            Collection<ScriptingRuleParameter> validateParams = new ArrayList<ScriptingRuleParameter>();

            validateParams.add(new ScriptingRuleParameterImpl( "transformedCredentials", transformedCredentials, Credential.class ));

            ScriptingRuleEngine sre = new ScriptingRuleEngine();
            ScriptingRuleExecutionOutcome outcome;

            try {
                outcome = sre.execute(validateRule, validateParams);
                Boolean error = (Boolean)outcome.getObject();

                if (error) {
                    throw new IllegalArgumentException("Error validating mapped credential entry");
                }

            } catch (Exception e) {
                logger.error("Credential Mapping Rule Failed...ignoring", e);
            }
        }
    }

}