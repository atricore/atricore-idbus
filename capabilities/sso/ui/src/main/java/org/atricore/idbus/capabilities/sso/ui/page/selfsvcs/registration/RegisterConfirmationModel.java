package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/8/13
 */
public class RegisterConfirmationModel implements Serializable {

    private String password;

    private String retypedPassword;

    private List<SecurityQuestionModel> securityQuestions = new ArrayList<SecurityQuestionModel>();
}
