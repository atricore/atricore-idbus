package org.atricore.idbus.kernel.main.mail;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public interface MailService {

    // TODO : Add multipart support, attachments, etc

    void send(String config, String from, String to, String subject, String message, String contentType);

    void sendAsync(String config, String from, String to, String subject, String message, String contentType);

    void send(String from, String to, String subject, String message, String contentType);

    void sendAsync(String from, String to, String subject, String message, String contentType);
}
