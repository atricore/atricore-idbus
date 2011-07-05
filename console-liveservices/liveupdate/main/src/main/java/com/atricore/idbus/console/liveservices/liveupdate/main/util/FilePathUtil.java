package com.atricore.idbus.console.liveservices.liveupdate.main.util;

import org.apache.commons.lang.SystemUtils;

public class FilePathUtil {

    public static String fixFilePath(String path) {
        String filePath = path;
		if (filePath != null && !filePath.startsWith("http")) {
	        if (filePath.startsWith("file:")) {
	        	int index = 5;
	        	while (filePath.charAt(index) == '/') {
	        		index++;
	        	}
	            filePath = "file:///" + filePath.substring(index, filePath.length());
	        } else {
	        	if (SystemUtils.IS_OS_WINDOWS) {
	        		filePath = "file:///" + filePath;
	        	} else {
	        		filePath = "file://" + filePath;
	        	}
	        }
	        filePath = filePath.replace("\\", "/");
            filePath = filePath.replace(" ", "%20");
		}
        return filePath;
    }
}
