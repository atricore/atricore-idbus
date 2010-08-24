package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class EmbeddedIdentitySource extends IdentitySource {

    private String idau;

    private String psp;

    private String pspTarget;

    public String getIdau() {
        return idau;
    }

    public void setIdau(String idau) {
        this.idau = idau;
    }

    public String getPsp() {
        return psp;
    }

    public void setPsp(String psp) {
        this.psp = psp;
    }

    public String getPspTarget() {
        return pspTarget;
    }

    public void setPspTarget(String pspTarget) {
        this.pspTarget = pspTarget;
    }
}
