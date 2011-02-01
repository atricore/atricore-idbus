package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EMailNotificationHandler implements NotificationHandler {

    private static final Log logger = LogFactory.getLog(EMailNotificationHandler.class);

    private NotificationSchemeStore store;

    private Mailer mailer;

    private MessageSource messageSource;
    
    public boolean canHandle(NotificationScheme scheme) {
        return scheme instanceof EMailNotificationScheme;
    }

    public void notify(Collection<UpdateDescriptorType> updates, NotificationScheme scheme) throws LiveUpdateException {
        EMailNotificationScheme emailScheme = (EMailNotificationScheme) scheme;
        List<UpdateDescriptorType> newUpdates = new ArrayList<UpdateDescriptorType>();
        List<String> newUpdatesIDs = new ArrayList<String>();

        // find new updates (check for threshold and processed updates)
        String[] processedUpdates = store.getProcessedUpdates(scheme.getName());
        for (UpdateDescriptorType update : updates) {
            if (update.getInstallableUnit().getUpdateNature().toString().equals(scheme.getThreshold()) &&
                    !ArrayUtils.contains(processedUpdates, update.getID())) {
                newUpdates.add(update);
                newUpdatesIDs.add(update.getID());
            }
        }

        // send emails
        if (newUpdates.size() > 0) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("header", "<img src='cid:headerImage'>");
            model.put("title", messageSource.getMessage("email.title", null, "UPDATES", null));
            model.put("updateStr", messageSource.getMessage("email.update", null, "Update", null));
            model.put("updateNatureStr", messageSource.getMessage("email.update.nature", null, "Update nature", null));
            model.put("descriptionStr", messageSource.getMessage("email.update.description", null, "Description", null));
            model.put("artifactsStr", messageSource.getMessage("email.update.artifacts", null, "Artifacts", null));
            model.put("updates", newUpdates);
            model.put("footer", "");

            Map<String, Resource> inlineResources = new HashMap<String, Resource>();
            inlineResources.put("headerImage", new ClassPathResource("images/a3c_logo.png"));

            String template = "/com/atricore/idbus/console/liveservices/liveupdate/main/notifications/templates/email-notification.vm";
            String subject = messageSource.getMessage("mail.subject", null, "JOSSO 2 Updates", null);
            String sender = "updates@atricore.com";

            mailer.sendTemplateHTMLEmail(emailScheme, sender, subject, template, model, inlineResources);

            store.addProcessedUpdates(scheme.getName(), newUpdatesIDs.toArray(new String[]{}));
        }
    }

    public void saveNotificationScheme(NotificationScheme scheme) throws LiveUpdateException {
        store.store(scheme);
    }

    public void removeNotificationScheme(NotificationScheme scheme) throws LiveUpdateException {
        store.remove(scheme.getName());
    }

    public void setStore(NotificationSchemeStore store) {
        this.store = store;
    }

    public void setMailer(Mailer mailer) {
        this.mailer = mailer;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
