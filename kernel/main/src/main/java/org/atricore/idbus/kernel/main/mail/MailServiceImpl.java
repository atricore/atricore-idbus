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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class MailServiceImpl implements MailService {

    private static final Log logger = LogFactory.getLog(MailServiceImpl.class);

    private List<MailSender> senders = new ArrayList<MailSender>();

    // TODO : Configure
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public void send(String config, String from, String to, String subject, String message, String contentType) {
        for (MailSender mailSender : senders) {
            if (mailSender.getName().equals(config)) {
                mailSender.send(from, to, subject, message, contentType);
                break;
            }
        }
    }

    public void sendAsync(String config, String from, String to, String subject, String message, String contentType) {

        for (MailSender mailSender : senders) {
            if (mailSender.getName().equals(config)) {
                SendAsync s = new SendAsync(mailSender, from, to, subject, message, contentType);
                executor.submit(s);
                break;
            }
        }
    }

    // ---------------------------------------------------------------------------------------

    public void send(String from, String to, String subject, String message, String contentType) {
        if (senders.size() > 0) {
            senders.get(0).send(from, to, subject, message, contentType);
            return;
        }

        logger.error("No senders configured !");

    }

    public void sendAsync(String from, String to, String subject, String message, String contentType) {
        if (senders.size() > 0) {
            sendAsync(senders.get(0).getName(), from, to, subject, message, contentType);
            return;
        }

        logger.error("No senders configured !");
    }

    // ---------------------------------------------------------------------------------------

    public List<MailSender> getSenders() {
        return senders;
    }

    public void setSenders(List<MailSender> senders) {
        this.senders = senders;
    }


    public class SendAsync implements Runnable {

        private MailSender sender;

        private String from;
        private String to;
        private String subject;
        private String message;
        private String contentType;

        public SendAsync(MailSender sender, String from, String to, String subject, String message, String contentType) {
            this.sender = sender;
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.message = message;
            this.contentType = contentType;

        }

        @Override
        public void run() {
            try {
                sender.send(from, to , subject, message, contentType);
            } catch (Exception e) {
                logger.error("Error sending email async [to: "+to+"] " + e.getMessage(), e);
            }
        }
    }
}
