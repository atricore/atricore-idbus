package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/10/13
 */
public class SecurityQuestion implements Serializable {

    private static final long serialVersionUID = -2547786148798923521L;

    private Long id;

    private String messageKey;

    private String defaultMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
