package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;
import com.atricore.idbus.console.lifecycle.main.spi.ApplianceMarshaller;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceDefinitionWalker;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ByteArrayResource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceSpringMarshaller implements ApplianceMarshaller {

    private static final Log logger = LogFactory.getLog(ApplianceSpringMarshaller.class);

    private IdentityApplianceDefinitionWalker walker ;

    public IdentityApplianceDefinitionWalker getWalker() {
        return walker;
    }

    public void setWalker(IdentityApplianceDefinitionWalker walker) {
        this.walker = walker;
    }

    public byte[] marshall(IdentityAppliance appliance) throws IdentityServerException {
        try {


            Object[] result = walker.walk(appliance.getIdApplianceDefinition(),
                    new ApplianceSpringMarshallerVisitor(appliance));

            Beans beans = (Beans) result[0];

            // User the class classloader (bundle)
            JAXBContext jaxbCtx = JAXBContext.newInstance("com.atricore.idbus.console.lifecycle.support.springmetadata.model",
                    getClass().getClassLoader());

            Marshaller m = jaxbCtx.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            // No good ... :(
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.springframework.org/schema/beans " +
                "http://www.springframework.org/schema/beans/spring-beans-2.5.xsd ");

            ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
            OutputStreamWriter writer = new OutputStreamWriter(baos);
            m.marshal(beans, writer);
            writer.flush();
            writer.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new IdentityServerException(e);
        }


    }

    public IdentityAppliance unmarshall(byte[] beans) throws IdentityServerException {
        try {

            // 1. Instantiate beans
            GenericApplicationContext ctx = new GenericApplicationContext();
            ctx.setClassLoader(getClass().getClassLoader());

            XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
            xmlReader.loadBeanDefinitions(new ByteArrayResource(beans));
            ctx.refresh();

            Map<String, IdentityAppliance> appliances = ctx.getBeansOfType(IdentityAppliance.class);

            if (appliances.size() < 1 )
                throw new IdentityServerException("No Identity Appliance found in the given descriptor!");

            if (appliances.size() > 1)
                throw new IdentityServerException("Only one Identity Appliance per descriptor is supported. (found "+appliances.size()+")");

            IdentityAppliance appliance = appliances.values().iterator().next();
            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();
            if (applianceDef == null)
                throw new IdentityServerException("Appliance must contain an Appliance Definition");


            return appliance;

        } catch (Exception e) {
            throw new IdentityServerException(e);
        }

    }
}
