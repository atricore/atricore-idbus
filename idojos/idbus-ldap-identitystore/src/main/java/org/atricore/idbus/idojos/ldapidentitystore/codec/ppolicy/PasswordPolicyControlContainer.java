package org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy;

import org.apache.directory.shared.asn1.ber.AbstractContainer;
import org.atricore.idbus.idojos.ldapidentitystore.ppolicy.PasswordPolicyResponseControl;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyControlContainer extends AbstractContainer
{
    /** syncDoneValue*/
    private PasswordPolicyResponseControl control;


    /**
     *
     * Creates a new SyncDoneValueControlContainer object.
     *
     */
    public PasswordPolicyControlContainer()
    {
        super();
        stateStack = new int[1];
        grammar = PasswordPolicyControlGrammar.getInstance();
        states = PasswordPolicyControlStatesEnum.getInstance();
    }


    /**
     * @return the PasswordPolicyResponseControl object
     */
    public PasswordPolicyResponseControl getPasswordPolicyControl()
    {
        return control;
    }


    /**
     * Set a PasswordPolicyResponseControl Object into the container. It will be completed
     * by the decoder.
     *
     * @param control the SyncDoneValueControlCodec to set.
     */
    public void setPasswordPolicyResponseControl( PasswordPolicyResponseControl control )
    {
        this.control = control;
    }


    /**
     * clean the container
     */
    @Override
    public void clean()
    {
        super.clean();
        control = null;
    }

}
