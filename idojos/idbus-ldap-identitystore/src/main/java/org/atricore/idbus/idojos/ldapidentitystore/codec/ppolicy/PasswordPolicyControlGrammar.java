package org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.ber.grammar.*;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.util.IntegerDecoder;
import org.apache.directory.shared.asn1.util.IntegerDecoderException;

/**
 * PasswordPolicyResponseValue ::= SEQUENCE {
 * warning [0] CHOICE {
 * timeBeforeExpiration [0] INTEGER (0 .. maxInt),
 * graceAuthNsRemaining [1] INTEGER (0 .. maxInt) } OPTIONAL,
 * error   [1] ENUMERATED {
     * passwordExpired             (0),
     * accountLocked               (1),
     * changeAfterReset            (2),
     * passwordModNotAllowed       (3),
     * mustSupplyOldPassword       (4),
     * insufficientPasswordQuality (5),
     * passwordTooShort            (6),
     * passwordTooYoung            (7),
     * passwordInHistory           (8) } OPTIONAL
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyControlGrammar extends AbstractGrammar {

    private static final Log logger = LogFactory.getLog(PasswordPolicyControlGrammar.class);

    public static final int WARNING_TAG = 0xA0;

    public static final int ERROR_TAG = 0x81;

    public static final int TIMEBEFOREEXPIRATION_TAG = 0x80;

    public static final int GRACEAUTHNREMAINING_TAG = 0x81;

    /**
     * The instance of grammar. PSearchControlGrammar is a singleton
     */
    private static IGrammar instance = new PasswordPolicyControlGrammar();

    private PasswordPolicyControlGrammar() {
        name = PasswordPolicyControlGrammar.class.getName();
        statesEnum = PasswordPolicyControlStatesEnum.getInstance();

        // Create the transitions table
        super.transitions = new GrammarTransition[PasswordPolicyControlStatesEnum.LAST_PPOLICYRESPONSEVALUE_STATE][256];

        /**
         * Transition from initial state to Psearch sequence
         * PPolicyResponseValue ::= SEQUENCE {
         *     ...
         *
         * Initialize the password policy control object
         */
        super.transitions[IStates.INIT_GRAMMAR_STATE][UniversalTag.SEQUENCE_TAG] =
                new GrammarTransition(IStates.INIT_GRAMMAR_STATE,
                        PasswordPolicyControlStatesEnum.PPOLICYRESPONSEVALUE_SEQUENCE_STATE,
                        UniversalTag.SEQUENCE_TAG, new GrammarAction("Initiaization") {
                            public void action(IAsn1Container container) throws DecoderException {
                                PasswordPolicyControlContainer ppolicyContainer = (PasswordPolicyControlContainer) container;

                                // As all the values are optional or defaulted, we can end here
                                ppolicyContainer.grammarEndAllowed(true);
                            }
                        });

        /**
         * Transition from START to warning
         * ...
         * warning [0] CHOICE {
         * ...
         */
        super.transitions[PasswordPolicyControlStatesEnum.PPOLICYRESPONSEVALUE_SEQUENCE_STATE][WARNING_TAG] =
                new GrammarTransition(PasswordPolicyControlStatesEnum.PPOLICYRESPONSEVALUE_SEQUENCE_STATE,
                        PasswordPolicyControlStatesEnum.WARNING_STATE,
                        WARNING_TAG, new GrammarAction("Set PPolicy Warning") {
                            public void action(IAsn1Container container) throws DecoderException {
                                PasswordPolicyControlContainer ppolicyContainer = (PasswordPolicyControlContainer) container;

                                // As all the values are optional or defaulted, we can end here
                                ppolicyContainer.grammarEndAllowed(true);
                            }
                        });

        /**
         * Transition from warning to timeBeforeExpiration
         * ...
         * timeBeforeExpiration [0] INTEGER (0 .. maxInt),
         * ...
         */
        super.transitions[PasswordPolicyControlStatesEnum.WARNING_STATE][TIMEBEFOREEXPIRATION_TAG] =
                new GrammarTransition(PasswordPolicyControlStatesEnum.WARNING_STATE,
                        PasswordPolicyControlStatesEnum.TIMEBEFOREEXPIRATION_STATE,
                        TIMEBEFOREEXPIRATION_TAG, new GrammarAction("Set Time Before Expiration Warning") {
                            public void action(IAsn1Container container) throws DecoderException {
                                PasswordPolicyControlContainer ppolicyContainer = ( PasswordPolicyControlContainer  ) container;
                                Value value = ppolicyContainer.getCurrentTLV().getValue();

                                try
                                {
                                    if (logger.isTraceEnabled())
                                        logger.trace("Set Time Before Expiration Warning : ");

                                    // Check that the value is into the allowed interval
                                    int wValue = IntegerDecoder.parse(value, Integer.MIN_VALUE, Integer.MAX_VALUE);

                                    if (logger.isTraceEnabled())
                                        logger.trace("Set Time Before Expiration Warning : " + wValue);

                                    ppolicyContainer.getPasswordPolicyControl().setWarningType(LDAPPasswordPolicyWarningType.TIME_BEFORE_EXPIRATION);
                                    ppolicyContainer.getPasswordPolicyControl().setWarningValue(wValue);

                                    // As all the values are optional or defaulted, we can end here
                                    ppolicyContainer.grammarEndAllowed(true);


                                } catch ( IntegerDecoderException e ) {
                                    logger.error( e.getMessage(),e );
                                    throw new DecoderException( e.getMessage() );
                                }
                            }
                        }
                    );
        
        /**
         * Transition from warning to graceAuthNsRemaining
         * ...
         * graceAuthNsRemaining [1] INTEGER (0 .. maxInt) } OPTIONAL,
         * ...
         */
        super.transitions[PasswordPolicyControlStatesEnum.WARNING_STATE][GRACEAUTHNREMAINING_TAG] =
                new GrammarTransition(PasswordPolicyControlStatesEnum.WARNING_STATE,
                        PasswordPolicyControlStatesEnum.GRACEAUTHNREMAINING_STATE,
                        GRACEAUTHNREMAINING_TAG, new GrammarAction("Set Grace Authns Remaining Warning") {
                            public void action(IAsn1Container container) throws DecoderException {
                                PasswordPolicyControlContainer ppolicyContainer = ( PasswordPolicyControlContainer  ) container;
                                Value value = ppolicyContainer.getCurrentTLV().getValue();

                                try
                                {

                                    if (logger.isTraceEnabled())
                                        logger.trace("Set Grace Authns Remaining Warning");

                                    // Check that the value is into the allowed interval
                                    int wValue = IntegerDecoder.parse(value, Integer.MIN_VALUE, Integer.MAX_VALUE);

                                    if (logger.isTraceEnabled())
                                        logger.trace("Set Grace Authns Remaining Warning : " + wValue);

                                    ppolicyContainer.getPasswordPolicyControl().setWarningType(LDAPPasswordPolicyWarningType.GRACE_AUTHNS_REMAINING);
                                    ppolicyContainer.getPasswordPolicyControl().setWarningValue(wValue);

                                    // As all the values are optional or defaulted, we can end here
                                    ppolicyContainer.grammarEndAllowed(true);

                                } catch ( IntegerDecoderException e ) {
                                    logger.error( e.getMessage(),e );
                                    throw new DecoderException( e.getMessage() );
                                }
                            }
                        }
                    );

        /**
         * Transition from warning to error
         * ...
         * error   [1] ENUMERATED {
         * ...
         */

        super.transitions[PasswordPolicyControlStatesEnum.PPOLICYRESPONSEVALUE_SEQUENCE_STATE][ERROR_TAG] =
                new GrammarTransition(PasswordPolicyControlStatesEnum.PPOLICYRESPONSEVALUE_SEQUENCE_STATE,
                        PasswordPolicyControlStatesEnum.ERROR_STATE,
                        ERROR_TAG, new GrammarAction("Set PPolicy Error") {
                            public void action(IAsn1Container container) throws DecoderException {
                                PasswordPolicyControlContainer ppolicyContainer = (PasswordPolicyControlContainer) container;
                                Value value = ppolicyContainer.getCurrentTLV().getValue();

                                try {

                                    if (logger.isTraceEnabled())
                                        logger.trace("Set PPolicy error");

                                    int errorTypeInt = IntegerDecoder.parse(value, 0, LDAPPasswordPolicyErrorType.PASSWORD_IN_HISTORY.intValue());

                                    if (logger.isTraceEnabled())
                                        logger.trace("Set PPolicy error : " + errorTypeInt);

                                    LDAPPasswordPolicyErrorType errorType = LDAPPasswordPolicyErrorType.getErrorType(errorTypeInt);

                                    if (logger.isTraceEnabled())
                                        logger.trace("Set PPolicy error : " + errorType.name());

                                    ppolicyContainer.getPasswordPolicyControl().setErrorType(errorType);

                                    // As all the values are optional or defaulted, we can end here
                                    ppolicyContainer.grammarEndAllowed(true);

                                } catch (IntegerDecoderException e) {
                                    logger.error(e.getMessage(), e);
                                    throw new DecoderException(e.getMessage());
                                }

                                // As all the values are optional or defaulted, we can end here
                                ppolicyContainer.grammarEndAllowed(true);
                            }
                        });



    }

    /**
     * @return the singleton instance of the SyncDoneValueControlGrammar
     */
    public static IGrammar getInstance() {
        return instance;
    }
}
