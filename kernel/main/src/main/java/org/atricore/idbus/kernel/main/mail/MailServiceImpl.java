package org.atricore.idbus.kernel.main.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class MailServiceImpl implements MailService {

    private static final Log logger = LogFactory.getLog(MailServiceImpl.class);

    private List<MailSender> senders = new ArrayList<MailSender>();

    public class SendEmail {

    }

    public void send(String config, String from, String to, String subject, String message, String contentType) {
        for (int i = 0; i < senders.size(); i++) {
            MailSender mailSender = senders.get(i);
            if (mailSender.getName().equals(config)) {
                mailSender.send(from, to, subject, message, contentType);
                break;
            }
        }
    }

    public void sendAsync(String config, String from, String to, String subject, String message, String contentType) {
        throw new UnsupportedOperationException("not implemented");
    }

    // ---------------------------------------------------------------------------------------

    public void send(String from, String to, String subject, String message, String contentType) {
        if (senders.size() > 0)
            senders.get(0).send(from, to, subject, message, contentType);

        logger.error("No senders configured !");

    }

    public void sendAsync(String from, String to, String subject, String message, String contentType) {
        if (senders.size() > 0)
            sendAsync(senders.get(0).getName(), from, to, subject, message, contentType);

        logger.error("No senders configured !");
    }

    // ---------------------------------------------------------------------------------------

    public List<MailSender> getSenders() {
        return senders;
    }

    public void setSenders(List<MailSender> senders) {
        this.senders = senders;
    }
}
