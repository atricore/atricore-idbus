package org.atricore.idbus.kernel.main.mail;

/**
 * @author: sgonzalez@atriocore.com
 */
public interface MailSender {

    void init();

    String getName();

    void send(String from, String to, String subject, String messageText, String contentType);

}
