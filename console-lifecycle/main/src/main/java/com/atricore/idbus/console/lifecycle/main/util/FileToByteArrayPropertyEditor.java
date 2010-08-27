package com.atricore.idbus.console.lifecycle.main.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyEditorSupport;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Loads file into byte[].
 * Used only when importing appliance definition from spring xml file (for loading keystore files).
 */
public class FileToByteArrayPropertyEditor extends PropertyEditorSupport {

    private static final Log logger = LogFactory.getLog(FileToByteArrayPropertyEditor.class);
    
    public void setAsText(String fileName) {
        try {
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);

            ByteArrayOutputStream baos = new ByteArrayOutputStream ();

            byte[] buf = new byte[4096];
            int read = fis.read(buf, 0, buf.length);
            while (read > 0) {
                baos.write(buf, 0, read);
                read = fis.read(buf, 0, buf.length); 
            }
            setValue(baos.toByteArray());
        } catch (Exception e) {
            logger.error("Cannot resolve file [" + fileName + "] :" + e.getMessage(), e);
        }
    }
}
