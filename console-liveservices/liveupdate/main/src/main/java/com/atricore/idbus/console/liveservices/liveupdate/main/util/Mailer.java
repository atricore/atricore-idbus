package com.atricore.idbus.console.liveservices.liveupdate.main.util;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.util.Map;

public class Mailer {

    private JavaMailSender mailSender;

	private VelocityEngine velocityEngine;

    public void sendTemplateHTMLEmail(final String sender, final String recipient, final String subject,
                final String template, final Map<String, Object> model) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setTo(recipient);
                message.setFrom(sender);
                message.setSubject(subject);
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, model);
                message.setText(text, true);
           }
        };
        mailSender.send(preparator);
    }

    public void sendTemplateMassEmail(String sender, String[] recipient, String fakeTo, String subject,
    		String template, Map<String, Object> model) {
    	SimpleMailMessage mailMessage = new SimpleMailMessage();
    	mailMessage.setFrom(sender);
        mailMessage.setTo(fakeTo);
        mailMessage.setBcc(recipient);
        mailMessage.setSubject(subject);
        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, model);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}
