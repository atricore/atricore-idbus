package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.util.Map;

public class Mailer {

    private JavaMailSenderImpl mailSender;

	private VelocityEngine velocityEngine;

    public void sendTemplateHTMLEmail(EMailNotificationScheme scheme,
                final String sender, final String subject,
                final String template, final Map<String, Object> model,
                final Map<String, Resource> inlineResources) {

        mailSender.setHost(scheme.getSmtpHost());
        mailSender.setUsername(scheme.getSmtpUsername());
        mailSender.setPassword(scheme.getSmtpPassword());
        mailSender.setPort(scheme.getSmtpPort());

        for (final String recipient : scheme.getAddresses()) {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    message.setTo(recipient);
                    message.setFrom(sender);
                    message.setSubject(subject);
                    String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, model);
                    message.setText(text, true);
                    if (inlineResources != null) {
                        for (Map.Entry<String, Resource> inlineResource : inlineResources.entrySet()) {
                            message.addInline(inlineResource.getKey(), inlineResource.getValue());
                        }
                    }
               }
            };
            mailSender.send(preparator);
        }
    }

    public void sendTemplateMassEmail(EMailNotificationScheme scheme,
            String sender, String[] recipient, String fakeTo, String subject,
    		String template, Map<String, Object> model) {

        mailSender.setHost(scheme.getSmtpHost());
        mailSender.setUsername(scheme.getSmtpUsername());
        mailSender.setPassword(scheme.getSmtpPassword());
        mailSender.setPort(scheme.getSmtpPort());

    	SimpleMailMessage mailMessage = new SimpleMailMessage();
    	mailMessage.setFrom(sender);
        mailMessage.setTo(fakeTo);
        mailMessage.setBcc(recipient);
        mailMessage.setSubject(subject);
        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, model);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}
