package com.atricore.idbus.console.settings.main.spi;

import java.io.Serializable;

public class LogConfigProperty implements Serializable {

    private static final long serialVersionUID = -429612617844821018L;

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
