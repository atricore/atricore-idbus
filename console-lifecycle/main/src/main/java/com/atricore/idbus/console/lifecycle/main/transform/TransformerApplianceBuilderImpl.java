package com.atricore.idbus.console.lifecycle.main.transform;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.spi.ApplianceBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;

import java.io.*;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class TransformerApplianceBuilderImpl implements ApplianceBuilder {

    private static final Log logger = LogFactory.getLog(TransformerApplianceBuilderImpl.class);

    private TransformationEngine engine;

    public TransformationEngine getEngine() {
        return engine;
    }

    public void setEngine(TransformationEngine engine) {
        this.engine = engine;
    }

    public IdentityAppliance build(IdentityAppliance appliance) {
        return buildAppliance(appliance).getProject().getIdAppliance();
    }

    public byte[] exportProject(IdentityAppliance appliance) {
        IdApplianceTransformationContext ctx = buildAppliance(appliance);

        IdApplianceProject prj = ctx.getProject();
        ProjectModuleLayout layout = prj.getRootModule().getLayout();

        if (layout.getWorkDir() != null) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ZipOutputStream zout = new ZipOutputStream(bout);

            try {
                String zipFilePath = appliance.getName() + "-1.0." + appliance.getIdApplianceDefinition().getRevision();
                zipDir(layout.getWorkDir(), zipFilePath, zout);
                //now save appliance binary definition
                ZipEntry anEntry = new ZipEntry(zipFilePath + File.separator + "definition" + File.separator + "appliance.bin");
                zout.putNextEntry(anEntry);
                zout.write(appliance.getIdApplianceDefinitionBin().getBytes(),
                           0,
                           appliance.getIdApplianceDefinitionBin().getBytes().length);
                zout.closeEntry();

                zout.finish();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return bout.toByteArray();
        }

        return null;
    }

    public byte[] exportMetadata(IdentityAppliance appliance, String providerName, String channelName) {
        IdApplianceTransformationContext ctx = buildAppliance(appliance);

        IdApplianceProject prj = ctx.getProject();
        ProjectModuleLayout layout = prj.getRootModule().getLayout();

        if (layout.getWorkDir() != null) {
            String idauPath = (String) ctx.get("idauPath");
            Provider provider = null;
            Channel channel = null;

            for (Provider p : appliance.getIdApplianceDefinition().getProviders()) {
                if (p.getName().equals(providerName)) {
                    provider = p;
                    if (channelName != null) {
                        Set<FederatedConnection> fedConns = new HashSet<FederatedConnection>();
                        fedConns.addAll(((FederatedProvider) provider).getFederatedConnectionsA());
                        fedConns.addAll(((FederatedProvider) provider).getFederatedConnectionsB());
                        for (FederatedConnection fedConn : fedConns) {
                            FederatedChannel fedChannelA = fedConn.getChannelA();
                            FederatedChannel fedChannelB = fedConn.getChannelB();
                            if (fedChannelA.getName().equals(channelName)) {
                                channel = fedChannelA;
                                break;
                            } else if (fedChannelB.getName().equals(channelName)) {
                                channel = fedChannelB;
                                break;
                            }
                        }
                    }
                    break;
                }
            }

            if (provider == null || (channelName != null && channel == null)) {
                logger.error("Error exporting SAML metadata: no provider or channel with the given name.");
                return new byte[0];
            }

            String providerBeanName = normalizeBeanName(provider.getName());
            String resourceName = providerBeanName;
            if (channel != null && channel.isOverrideProviderSetup()) {
                resourceName = normalizeBeanName(channel.getName());
            }

            String metadataFile = layout.getWorkDir() + "/idau/src/main/resources/" +
                    idauPath + providerBeanName + "/" + resourceName + "-samlr2-metadata.xml";

            FileInputStream is = null;
            try {
                File file = new File(new URI(metadataFile));
                is = new FileInputStream(file);
                return IOUtils.toByteArray(is);
            } catch (Exception e) {
                logger.error("Error exporting SAML metadata: error reading file [" + metadataFile + "].");
                return new byte[0];
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        return null;
    }

    public byte[] exportJosso1Configuration(IdentityAppliance appliance, String execEnvName) {
        IdApplianceTransformationContext ctx = buildAppliance(appliance);

        IdApplianceProject prj = ctx.getProject();
        ProjectModuleLayout layout = prj.getRootModule().getLayout();

        if (layout.getWorkDir() != null) {
            ExecutionEnvironment execEnv = null;

            for (ExecutionEnvironment executionEnvironment : appliance.getIdApplianceDefinition().getExecutionEnvironments()) {
                if (executionEnvironment.getName().equals(execEnvName)) {
                    execEnv = executionEnvironment;
                    break;
                }
            }

            if (execEnv == null) {
                logger.error("Error exporting JOSSO1 configuration: no execution environment with the given name.");
                return new byte[0];
            }

            String execEnvBeanName = normalizeBeanName(execEnv.getName());

            String configFileName = "josso-agent-";
            if (execEnv.getPlatformId().startsWith("iis")) {
                configFileName += "config.ini";
            } else {
                configFileName += execEnvBeanName + "-config.xml";
            }
            String configFile = layout.getWorkDir() + "/idau/src/main/resources/META-INF/spring/" +
                    execEnvBeanName + "/josso/" + configFileName;

            FileInputStream is = null;
            try {
                File file = new File(new URI(configFile));
                is = new FileInputStream(file);
                return IOUtils.toByteArray(is);
            } catch (Exception e) {
                logger.error("Error exporting JOSSO1 configuration: error reading file [" + configFile + "].");
                return new byte[0];
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        return null;
    }

    protected IdApplianceTransformationContext buildAppliance(IdentityAppliance appliance) {

        try {
            IdentityApplianceDeployment deployment = appliance.getIdApplianceDeployment();
            if (deployment == null) {

                if (logger.isDebugEnabled())
                    logger.debug("Creating new IdentityApplianceDeployment instance");
                deployment = new IdentityApplianceDeployment();
                appliance.setIdApplianceDeployment(deployment);
            }

            return engine.transform(appliance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void zipDir(FileObject dir, String parentPath, ZipOutputStream zout) {
        try {
            // get a listing of the directory content
            FileObject[] files = dir.getChildren();
            byte[] readBuffer = new byte[10240];
            int bytesIn = 0;

            boolean hasPom = false;
            for (FileObject file : files) {
                if (file.getName().getBaseName().equals("pom.xml")) {
                    hasPom = true;
                    break;
                }
            }

            // loop through files and zip them
            for (FileObject file : files) {

                String fileName = file.getName().getBaseName();
                if (file.getType() == FileType.FOLDER && hasPom && fileName.equals("target")) {
                    // Do not zip any target folders ...
                    continue;
                }

                String zipPath = parentPath + File.separator + fileName;
                if (file.getType() == FileType.FOLDER) {
                    // if the FileObject is a directory, call this
                    // function again to add its content recursively
                    zipDir(file, zipPath, zout);
                    continue;
                }
                // FileObject is a file, add it as ZipEntry
                InputStream fis = file.getContent().getInputStream();
                ZipEntry anEntry = new ZipEntry(zipPath);
                // place the zip entry in the ZipOutputStream object
                zout.putNextEntry(anEntry);
                // now write the content of the file to the ZipOutputStream
                while ((bytesIn = fis.read(readBuffer)) != -1) {
                    zout.write(readBuffer, 0, bytesIn);
                }
                zout.closeEntry();
                // close the Stream
                fis.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String normalizeBeanName(String name) {
        String regex = "[ .]";
        return name.replaceAll(regex, "-").toLowerCase();
    }
}
