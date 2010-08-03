package com.atricore.idbus.console.lifecycle.main.util;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.FileInputStream;

/**
 * Loads file into byte[].
 * Used only when importing appliance definition from spring xml file (for loading keystore files).
 */
public class FileToByteArrayPropertyEditor extends PropertyEditorSupport {
    
    public void setAsText(String text) {
        try {
            File file = new File(text);
            FileInputStream fis = new FileInputStream(file);
            byte[] fileContent = new byte[(int)file.length()];
            fis.read(fileContent);
            setValue(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
