package com.atricore.idbus.console.services.dto.settings;

import java.io.Serializable;

public class LogConfigPropertyDTO implements Serializable {

    private static final long serialVersionUID = -4218654230136119551L;

    private String category;
    
    private String level;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
