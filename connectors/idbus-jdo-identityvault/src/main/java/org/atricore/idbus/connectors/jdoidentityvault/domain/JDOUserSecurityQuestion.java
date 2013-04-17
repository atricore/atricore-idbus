package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/10/13
 */
public class JDOUserSecurityQuestion implements Serializable {

    private static final long serialVersionUID = 948541836585275998L;

    private Long id;

    private JDOSecurityQuestion question;

    private String answer;

    private String hashing;

    private String encryption;

    private String customMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JDOSecurityQuestion getQuestion() {
        return question;
    }

    public void setQuestion(JDOSecurityQuestion question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getHashing() {
        return hashing;
    }

    public void setHashing(String hashing) {
        this.hashing = hashing;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }
}
