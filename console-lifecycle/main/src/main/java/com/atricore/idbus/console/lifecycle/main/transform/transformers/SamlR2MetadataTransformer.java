package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;

import java.io.InputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2MetadataTransformer extends AbstractTransformer {

    private String baseSrcPath = "/org/atricore/idbus/examples/simplefederation/idau/";

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityApplianceDefinition;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        try {
            IdProjectModule module = event.getContext().getCurrentModule();
            String baseDestPath = (String) event.getContext().get("baseIdauDestPath");

            // idp1-samlr2-metadata.xml
            IdProjectResource<InputStream> idp1MetadataXml = new IdProjectResource<InputStream>(idGen.generateId(),
                    baseDestPath + "idp1/", "idp1-samlr2-metadata.xml", "copy",
                    getClass().getResourceAsStream(baseSrcPath + "idp1/idp-1-samlr2-metadata.xml"));
            idp1MetadataXml.setClassifier("copy");
            module.addResource(idp1MetadataXml);

            // idp1-keystore.jks
            IdProjectResource<InputStream> idp1Keystore = new IdProjectResource<InputStream>(idGen.generateId(),
                    baseDestPath + "idp1/", "idp1-keystore.jks", "copy",
                    getClass().getResourceAsStream(baseSrcPath + "idp1/idp1-keystore.jks"));
            idp1Keystore.setClassifier("copy");
            module.addResource(idp1Keystore);

            // sp1-samlr2-metadata.xml
            IdProjectResource<InputStream> sp1MetadataXml = new IdProjectResource<InputStream>(idGen.generateId(),
                    baseDestPath + "sp1/", "sp1-samlr2-metadata.xml", "copy",
                    getClass().getResourceAsStream(baseSrcPath + "sp1/sp-1-samlr2-metadata.xml"));
            sp1MetadataXml.setClassifier("copy");
            module.addResource(sp1MetadataXml);
            
            // sp1-keystore.jks
            IdProjectResource<InputStream> sp1Keystore = new IdProjectResource<InputStream>(idGen.generateId(),
                    baseDestPath + "sp1/", "sp1-keystore.jks", "copy",
                    getClass().getResourceAsStream(baseSrcPath + "sp1/sp1-keystore.jks"));
            sp1Keystore.setClassifier("copy");
            module.addResource(sp1Keystore);

            // atricore-users.xml
            IdProjectResource<InputStream> atricoreUsers = new IdProjectResource<InputStream>(idGen.generateId(),
                    baseDestPath, "atricore-users.xml", "copy",
                    getClass().getResourceAsStream(baseSrcPath + "atricore-users.xml"));
            atricoreUsers.setClassifier("copy");
            module.addResource(atricoreUsers);

            // atricore-credentials.xml
            IdProjectResource<InputStream> atricoreCredentials = new IdProjectResource<InputStream>(idGen.generateId(),
                    baseDestPath, "atricore-credentials.xml", "copy",
                    getClass().getResourceAsStream(baseSrcPath + "atricore-credentials.xml"));
            atricoreCredentials.setClassifier("copy");
            module.addResource(atricoreCredentials);
        } catch (Exception e) {
            throw new TransformException(e);
        }
    }
}
