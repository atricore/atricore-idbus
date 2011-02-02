package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Map;

public class Mailer {

    private static final Log logger = LogFactory.getLog(Mailer.class);

    private JavaMailSenderImpl mailSender;

	private VelocityEngine velocityEngine;

    public void init() {
        // add handlers for main MIME types
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public void sendTemplateHTMLEmail(EMailNotificationScheme scheme,
                final String sender, final String subject,
                final String template, final VelocityContext veCtx,
                final Map<String, String> inlineResources) {

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

                    InputStream is = null;

                    try {
                        // Load the template file because velocity is not handling OSGi classloader very well.
                        is = getClass().getResourceAsStream(template);

                        if (is == null) {
                            logger.error("Velocity template not found: " + template);
                            throw new LiveUpdateException("Velocity template not found: " + template);
                        }

                        Reader reader = new InputStreamReader(is);
                        Writer writer = new StringWriter();

                        velocityEngine.evaluate(veCtx, writer, "Mailer", reader);

                        writer.flush();
                        writer.close();

                        message.setText(writer.toString(), true);
                    } catch (Exception e) {
                        throw new LiveUpdateException ("Cannot evaluate template [" + template + "]: " + e.getMessage(), e);
                    } finally {
                        if (is != null) try { is.close(); } catch (IOException e) { /**/}
                    }

                    if (inlineResources != null) {
                        for (Map.Entry<String, String> inlineResource : inlineResources.entrySet()) {
                            message.addInline(inlineResource.getKey(), new ClassPathResource(inlineResource.getValue(), getClass().getClassLoader()));
                        }
                    }
               }
            };
            mailSender.send(preparator);
        }
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}
