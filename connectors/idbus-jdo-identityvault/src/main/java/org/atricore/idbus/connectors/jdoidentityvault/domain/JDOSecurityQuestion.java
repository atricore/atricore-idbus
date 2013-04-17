package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/10/13
 */
public class JDOSecurityQuestion implements Serializable {

    private static final long serialVersionUID = 8243541836585275998L;

    private Long id;
    private String messageKey;
    private String defaultMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDOSecurityQuestion)) return false;

        JDOSecurityQuestion that = (JDOSecurityQuestion) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}

