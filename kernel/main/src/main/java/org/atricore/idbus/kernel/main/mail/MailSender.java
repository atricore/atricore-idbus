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
 */
public interface MailSender {

    void init();

    String getName();

    void send(String from, String to, String subject, String messageText, String contentType);

}
