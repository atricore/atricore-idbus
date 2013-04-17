package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import org.atricore.idbus.kernel.main.provisioning.domain.SecurityQuestion;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/5/13
 */
public class RegistrationModel implements Serializable {

    private String password;

    private String newPassword;

    private String retypedPassword;


    private SecurityQuestion secQuestion1;

    private boolean useCustomSecQuestion1;

    private String customSecQuestion1;

    private String secAnswer1;


    private SecurityQuestion  secQuestion2;

    private boolean useCustomSecQuestion2;

    private String customSecQuestion2;

    private String secAnswer2;


    private SecurityQuestion  secQuestion3;

    private boolean useCustomSecQuestion3;

    private String customSecQuestion3;

    private String secAnswer3;

    private SecurityQuestion  secQuestion4;

    private boolean useCustomSecQuestion4;

    private String customSecQuestion4;

    private String secAnswer4;

    private SecurityQuestion  secQuestion5;

    private boolean useCustomSecQuestion5;

    private String customSecQuestion5;

    private String secAnswer5
            ;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }

    public SecurityQuestion  getSecQuestion1() {
        return secQuestion1;
    }

    public void setSecQuestion1(SecurityQuestion  secQuestion1) {
        this.secQuestion1 = secQuestion1;
    }

    public String getCustomSecQuestion1() {
        return customSecQuestion1;
    }

    public void setCustomSecQuestion1(String customSecQuestion1) {
        this.customSecQuestion1 = customSecQuestion1;
    }

    public String getSecAnswer1() {
        return secAnswer1;
    }

    public void setSecAnswer1(String secAnswer1) {
        this.secAnswer1 = secAnswer1;
    }

    public boolean isUseCustomSecQuestion1() {
        return useCustomSecQuestion1;
    }

    public void setUseCustomSecQuestion1(boolean useCustomSecQuestion1) {
        this.useCustomSecQuestion1 = useCustomSecQuestion1;
    }

    public SecurityQuestion  getSecQuestion2() {
        return secQuestion2;
    }

    public void setSecQuestion2(SecurityQuestion  secQuestion2) {
        this.secQuestion2 = secQuestion2;
    }

    public boolean isUseCustomSecQuestion2() {
        return useCustomSecQuestion2;
    }

    public void setUseCustomSecQuestion2(boolean useCustomSecQuestion2) {
        this.useCustomSecQuestion2 = useCustomSecQuestion2;
    }

    public String getCustomSecQuestion2() {
        return customSecQuestion2;
    }

    public void setCustomSecQuestion2(String customSecQuestion2) {
        this.customSecQuestion2 = customSecQuestion2;
    }

    public String getSecAnswer2() {
        return secAnswer2;
    }

    public void setSecAnswer2(String secAnswer2) {
        this.secAnswer2 = secAnswer2;
    }


    public SecurityQuestion  getSecQuestion3() {
        return secQuestion3;
    }

    public void setSecQuestion3(SecurityQuestion  secQuestion3) {
        this.secQuestion3 = secQuestion3;
    }

    public boolean isUseCustomSecQuestion3() {
        return useCustomSecQuestion3;
    }

    public void setUseCustomSecQuestion3(boolean useCustomSecQuestion3) {
        this.useCustomSecQuestion3 = useCustomSecQuestion3;
    }

    public String getCustomSecQuestion3() {
        return customSecQuestion3;
    }

    public void setCustomSecQuestion3(String customSecQuestion3) {
        this.customSecQuestion3 = customSecQuestion3;
    }

    public String getSecAnswer3() {
        return secAnswer3;
    }

    public void setSecAnswer3(String secAnswer3) {
        this.secAnswer3 = secAnswer3;
    }


    public SecurityQuestion getSecQuestion4() {
        return secQuestion4;
    }

    public void setSecQuestion4(SecurityQuestion secQuestion4) {
        this.secQuestion4 = secQuestion4;
    }

    public boolean isUseCustomSecQuestion4() {
        return useCustomSecQuestion4;
    }

    public void setUseCustomSecQuestion4(boolean useCustomSecQuestion4) {
        this.useCustomSecQuestion4 = useCustomSecQuestion4;
    }

    public String getCustomSecQuestion4() {
        return customSecQuestion4;
    }

    public void setCustomSecQuestion4(String customSecQuestion4) {
        this.customSecQuestion4 = customSecQuestion4;
    }

    public String getSecAnswer4() {
        return secAnswer4;
    }

    public void setSecAnswer4(String secAnswer4) {
        this.secAnswer4 = secAnswer4;
    }

    public SecurityQuestion getSecQuestion5() {
        return secQuestion5;
    }

    public void setSecQuestion5(SecurityQuestion secQuestion5) {
        this.secQuestion5 = secQuestion5;
    }

    public boolean isUseCustomSecQuestion5() {
        return useCustomSecQuestion5;
    }

    public void setUseCustomSecQuestion5(boolean useCustomSecQuestion5) {
        this.useCustomSecQuestion5 = useCustomSecQuestion5;
    }

    public String getCustomSecQuestion5() {
        return customSecQuestion5;
    }

    public void setCustomSecQuestion5(String customSecQuestion5) {
        this.customSecQuestion5 = customSecQuestion5;
    }

    public String getSecAnswer5() {
        return secAnswer5;
    }

    public void setSecAnswer5(String secAnswer5) {
        this.secAnswer5 = secAnswer5;
    }
}
