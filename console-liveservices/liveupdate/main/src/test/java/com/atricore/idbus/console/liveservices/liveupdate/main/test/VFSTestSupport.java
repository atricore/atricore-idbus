package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;

public abstract class VFSTestSupport {

    protected static ApplicationContext applicationContext;

    protected static FileSystemManager fsManager;

    @Before
    public void setup() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    protected static FileSystemManager getFileSystemManager() {
        if (fsManager == null) {
            try {
                fsManager = VFS.getManager();
            } catch (FileSystemException e) {
                throw new RuntimeException(e);
            }
        }
        return fsManager;
    }
}
