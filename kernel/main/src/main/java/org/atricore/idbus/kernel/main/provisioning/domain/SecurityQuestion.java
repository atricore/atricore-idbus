package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/10/13
 */
public class SecurityQuestion implements Serializable {

    private static final long serialVersionUID = -2547786148798923521L;

    private String id;

    private String messageKey;

    private String defaultMessage;


    public SecurityQuestion() {

    }

    public SecurityQuestion(String id, String messageKey, String defaultMessage) {
        this.id = id;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
