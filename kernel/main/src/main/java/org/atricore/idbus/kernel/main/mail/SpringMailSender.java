package org.atricore.idbus.kernel.main.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by sgonzalez.
 */
public class SpringMailSender implements MailSender {

    private static final Log logger = LogFactory.getLog(SpringMailSender.class);


    private String name;

    private JavaMailSenderImpl springMailSender;

    @Override
    public void init() {

    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JavaMailSenderImpl getSpringMailSender() {
        return springMailSender;
    }

    public void setSpringMailSender(JavaMailSenderImpl springMailSender) {
        this.springMailSender = springMailSender;
    }

    @Override
    public void send(String from, String to, String subject, String body, String contentType) {

        try {

            MimeMessage message = springMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setText(body, contentType != null && contentType.contains("html"));
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);

            springMailSender.send(message);

        } catch (MessagingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }


    }


}
