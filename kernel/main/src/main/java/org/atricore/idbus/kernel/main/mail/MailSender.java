package org.atricore.idbus.kernel.main.mail;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class MailSender {

    private static Log logger = LogFactory.getLog(MailSender.class);

    private String name;

    private String host;

    private String port;

    private String username;

    private String password;

    private boolean startTls;

    private Properties properties;

    private void init() {

        properties = new Properties();

        logger.info("Initializing mail sender ["+name+"] with host [" + host + "], username ["+username+"], password [*]");

        properties.setProperty("mail.smtp.host", host);
        if (port != null && NumberUtils.toInt(port) > 0)
            properties.setProperty("mail.smtp.port", port);
        if (username != null)
            properties.setProperty("mail.username", username);
        if (password != null)
            properties.setProperty("mail.password", password);
        if (startTls)
            properties.setProperty("mail.smtp.starttls.enable", "true");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStartTls() {
        return startTls;
    }

    public void setStartTls(boolean startTls) {
        this.startTls = startTls;
    }

    public void send(String from, String to, String subject, String messageText, String contentType) {

        // Due to the 'mailcap' issue between javax.mail and javax.activation in OSGi, les't play with the classloaders
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();

        Session session = Session.getDefaultInstance(properties);

        try{
            // Set the ClassLoader to the javax.mail bundle loader.
            Thread.currentThread().setContextClassLoader(javax.mail.Session.class.getClassLoader());

            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message using MIME

            MimeBodyPart messagePart = new MimeBodyPart();
            messagePart.setText(messageText, "utf-8");
            messagePart.setHeader("Content-Type",contentType + "; charset=\"utf-8\"");
            messagePart.setHeader("Content-Transfer-Encoding", "quoted-printable");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messagePart);

            message.setContent(multipart);
            message.setSentDate(new Date());

            // Send message

            if (username != null) {

                Transport t = session.getTransport("smtp");
                try {
                    t.connect(username, password);
                    t.sendMessage(message, message.getAllRecipients());
                } finally {
                    t.close();
                }

            } else {
                Transport.send(message);
            }

            if (logger.isTraceEnabled())
                logger.trace("Sent message successfully: " + message.getAllRecipients());

        }catch (MessagingException mex) {
            logger.error(mex.getMessage(), mex);

        } finally {
            // Reset the ClassLoader where it should be.
            Thread.currentThread().setContextClassLoader(tcl);
        }
    }

}
