package org.atricore.idbus.capabilities.sso.ui.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebBrandingEvent {
    
    public static final int PUBLISH = 0;

    public static final int REMOVE = 1;

    private int type;
    
    private String brandingId;

    public WebBrandingEvent(int type, String brandingId) {
        this.type = type;
        this.brandingId = brandingId;
    }

    public int getType() {
        return type;
    }

    public String getBrandingId() {
        return brandingId;
    }
}
