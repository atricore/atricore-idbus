package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import org.atricore.idbus.capabilities.sso.support.core.AttributeNameFormat;

public class AttributeMapping {

    private String attrName;

    private String reportedAttrName;

    private String reportedAttrNameFormat;

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getReportedAttrName() {
        return reportedAttrName;
    }

    public void setReportedAttrName(String reportedAttrName) {
        this.reportedAttrName = reportedAttrName;
    }

    public String getReportedAttrNameFormat() {
        return reportedAttrNameFormat;
    }

    public void setReportedAttrNameFormat(String reportedAttrNameFormat) {
        this.reportedAttrNameFormat = reportedAttrNameFormat;
    }
}
