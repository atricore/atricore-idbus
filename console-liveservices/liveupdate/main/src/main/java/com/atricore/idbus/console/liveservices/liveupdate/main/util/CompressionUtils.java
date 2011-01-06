package com.atricore.idbus.console.liveservices.liveupdate.main.util;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CompressionUtils {

    public static File unpackZipFile(InputStream in, String destFolder) throws Exception {
        ZipInputStream zin = new ZipInputStream(in);

        final int BUFFER_SIZE = 2048;
        BufferedOutputStream dest;
        ZipEntry entry;
        File unpackedDir = null;

        while ((entry = zin.getNextEntry()) != null) {
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            File file = new File(new URI(destFolder + "/" + entry.getName()));
            if (entry.isDirectory()) {
            	file.mkdirs();
            } else {
            	file.getParentFile().mkdirs();
            	FileOutputStream fos = new FileOutputStream(file, false);
	            dest = new BufferedOutputStream(fos, BUFFER_SIZE);
	            while ((count = zin.read(data, 0, BUFFER_SIZE)) != -1) {
	               dest.write(data, 0, count);
	            }
	            dest.flush();
	            dest.close();
            }
            if (unpackedDir == null) {
            	File parent = file;
            	while (parent.getParentFile() != null && !destFolder.equals("file://" + parent.getParentFile().getPath())) {
            		parent = parent.getParentFile();
            	}
            	unpackedDir = parent;
            }
        }

        return unpackedDir;
    }

    public static File unpackTarGzFile(InputStream in, String destFolder) throws Exception {
        GZIPInputStream zin = new GZIPInputStream(in);
        TarArchiveInputStream tar = new TarArchiveInputStream(zin);

        final int BUFFER_SIZE = 2048;
        BufferedOutputStream dest;
        ArchiveEntry entry;
        File unpackedDir = null;
        
        while ((entry = tar.getNextEntry()) != null) {
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            File file = new File(new URI(destFolder + "/" + entry.getName()));
            if (entry.isDirectory()) {
            	file.mkdirs();
            } else {
            	file.getParentFile().mkdirs();
            	FileOutputStream fos = new FileOutputStream(file, false);
	            dest = new BufferedOutputStream(fos, BUFFER_SIZE);
	            while ((count = tar.read(data, 0, BUFFER_SIZE)) != -1) {
	               dest.write(data, 0, count);
	            }
	            dest.flush();
	            dest.close();
            }
            if (unpackedDir == null) {
            	File parent = file;
            	while (parent.getParentFile() != null && !destFolder.equals("file://" + parent.getParentFile().getPath())) {
            		parent = parent.getParentFile();
            	}
            	unpackedDir = parent;
            }
        }

        return unpackedDir;
    }
}
