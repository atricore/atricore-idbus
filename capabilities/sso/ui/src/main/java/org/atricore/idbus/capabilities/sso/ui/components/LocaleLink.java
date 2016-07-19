package org.atricore.idbus.capabilities.sso.ui.components;

import org.apache.wicket.markup.html.link.Link;
import org.springframework.util.StringUtils;

import java.util.Locale;

public class LocaleLink extends Link {

    private static final long serialVersionUID = -7106781144768994483L;

    private final Locale locale;

    public LocaleLink(final String wicketId, final String localeString) {
        super(wicketId);
        this.locale = StringUtils.parseLocaleString(localeString);
    }

    public LocaleLink(final String wicketId, final Locale locale) {
        super(wicketId);
        this.locale = locale;
    }

    @Override
    public void onClick() {
        this.getSession().setLocale(this.locale);
    }

    @Override
    public boolean isEnabled() {
        return this.locale != null && !this.locale.equals(this.getSession().getLocale());
    }
}
