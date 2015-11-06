package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/10/13
 */
public class UserSecurityQuestion implements Serializable {

    private static final long serialVersionUID = 1324996148798290707L;

    private String id;

    private SecurityQuestion question;

    private String answer;

    private String hashing;

    private String encryption;

    private String customMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SecurityQuestion getQuestion() {
        return question;
    }

    public void setQuestion(SecurityQuestion question) {
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
