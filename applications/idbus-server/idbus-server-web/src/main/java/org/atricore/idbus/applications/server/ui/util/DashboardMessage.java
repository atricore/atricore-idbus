package org.atricore.idbus.applications.server.ui.util;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DashboardMessage {

    private String msgKey;

    private List<Object> values;

    public DashboardMessage(String msgKey, List<Object> values) {
        this.msgKey = msgKey;
        this.values = values;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public List<Object> getValues() {
        return values;
    }
}
