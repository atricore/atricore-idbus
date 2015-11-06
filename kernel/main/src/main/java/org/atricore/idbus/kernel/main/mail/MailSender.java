package org.atricore.idbus.kernel.main.mail;

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

    private String username;

    private String password;

    private Properties properties;
    private String port;

    private String auth;

    private String starttlsEnable;

    private void init() {

        properties = new Properties();

        logger.info("Initializing mail sender [" + name + "] with host [" + host + "], username [" + username + "], password [*]");

        // SMTP Properties:
        properties.setProperty("mail.smtp.host", host);

        if (port != null)
            properties.setProperty("mail.smtp.port", port);

        if (auth != null)
            properties.setProperty("mail.smtp.auth", auth);

        if (starttlsEnable != null)
            properties.setProperty("mail.smtp.starttls.enable", starttlsEnable);

        if (username != null)
            properties.setProperty("mail.username", username);

        if (password != null)
            properties.setProperty("mail.password", password);

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

    public void send(String from, String to, String subject, String messageText, String contentType) {

        boolean tlsEnable = starttlsEnable != null ? Boolean.parseBoolean(starttlsEnable) : false;

        // Due to the 'mailcap' issue between javax.mail and javax.activation in OSGi, les't play with the classloaders
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();

        Session session = null;

        if (!tlsEnable) {
            session = Session.getDefaultInstance(properties);
        } else {
            session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
        }

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

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getAuth() {
        return auth;
    }

    public void setStarttlsEnable(String starttlsEnable) {
        this.starttlsEnable = starttlsEnable;
    }

    public String getStarttlsEnable() {
        return starttlsEnable;
    }
}
