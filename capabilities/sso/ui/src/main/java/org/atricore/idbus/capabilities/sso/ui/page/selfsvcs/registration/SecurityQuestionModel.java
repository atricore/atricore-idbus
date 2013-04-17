package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/8/13
 */
public class SecurityQuestionModel implements Serializable {

    private String key;

    private String description;

    private String messageKey;

    private String answer;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
