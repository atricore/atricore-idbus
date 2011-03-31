package com.atricore.idbus.console.licensing.command;

import com.atricore.idbus.console.licensing.main.InvalidLicenseException;
import com.atricore.idbus.console.licensing.main.LicenseManager;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "licensing", name = "activate", description = "Activate new product license")
public class ActivateLicenseCommand extends LicenseCommandSupport {

    @Option(name = "-l", aliases = "--license", description = "Path to the license file", required = true, multiValued = false)
    String licenseFile;

    @Override
    protected Object doExecute(LicenseManager svc) throws Exception {

        File license = new File(licenseFile);
        long length = license.length();
        if (length > Integer.MAX_VALUE) {
            throw new InvalidLicenseException("License file to long!");
        }

        FileInputStream fis = new FileInputStream(license);
        byte[] bytes = new byte[(int) license.length()];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=fis.read(bytes, offset, Math.min(bytes.length - offset, 512*1024))) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new InvalidLicenseException("Could not completely read file " + license.getName());
        }

        fis.close();
        svc.activateLicense(bytes);

        return null;
    }
}
