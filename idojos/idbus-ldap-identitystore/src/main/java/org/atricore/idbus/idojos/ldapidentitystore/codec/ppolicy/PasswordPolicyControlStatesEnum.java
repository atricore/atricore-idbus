package org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy;

import org.apache.directory.shared.asn1.ber.grammar.IGrammar;
import org.apache.directory.shared.asn1.ber.grammar.IStates;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyControlStatesEnum implements IStates
{
    // ~ Static fields/initializers
    // -----------------------------------------------------------------

    // =========================================================================
    // Persistent search control grammar states
    // =========================================================================
    /** Initial state */
    public static final int START_STATE = 0;

    /** Sequence Value */
    public static final int PPOLICYRESPONSEVALUE_SEQUENCE_STATE = 1;

    /** warning */
    public static final int WARNING_STATE = 2;

    /** Time Before Expiration **/
    public static final int TIMEBEFOREEXPIRATION_STATE  = 3;

    /** Grace Authentications Remaining **/
    public static final int GRACEAUTHNREMAINING_STATE = 4;

    /** changesOnly Value */
    public static final int ERROR_STATE = 5;

    /** terminal state */
    public static final int LAST_PPOLICYRESPONSEVALUE_STATE = 6;

    // =========================================================================
    // States debug strings
    // =========================================================================
    /** A string representation of all the states */
    private static String[] PPolicyResponseValueString = new String[]
        {
        "START_STATE",
        "PPOLICYRESPONSEVALUE_SEQUENCE_STATE",
        "WARNING_STATE",
        "TIMEBEFOREEXPIRATION_STATE",
        "GRACEAUTHNREMAINING_STATE",
        "ERROR_STATE",
        "LAST_PPOLICYRESPONSEVALUE_STATE"
        };

    /** The instance */
    private static PasswordPolicyControlStatesEnum instance = new PasswordPolicyControlStatesEnum();


    // ~ Constructors
    // -------------------------------------------------------------------------------

    /**
     * This is a private constructor. This class is a singleton
     */
    private PasswordPolicyControlStatesEnum()
    {
    }


    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * Get an instance of this class
     *
     * @return An instance on this class
     */
    public static IStates getInstance()
    {
        return instance;
    }


    /**
     * Get the grammar name
     *
     * @param grammar The grammar code
     * @return The grammar name
     */
    public String getGrammarName( int grammar )
    {
        return "PPOLICYCONTROL_GRAMMAR";
    }


    /**
     * Get the grammar name
     *
     * @param grammar The grammar class
     * @return The grammar name
     */
    public String getGrammarName( IGrammar grammar )
    {
        if ( grammar instanceof PasswordPolicyControlGrammar )
        {
            return "PPOLICYCONTROL_GRAMMAR";
        }

        return "UNKNOWN GRAMMAR";
    }


    /**
     * Get the string representing the state
     *
     * @param state The state number
     * @return The String representing the state
     */
    public String getState( int state )
    {
        return ( ( state == GRAMMAR_END ) ? "LAST_PPOLICYRESPONSEVALUE_STATE" : PPolicyResponseValueString[state] );
    }
}
