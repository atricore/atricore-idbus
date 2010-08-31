/*
 * Copyright (c) 2010., Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceProject;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.util.XmlApplicationContextEnhancer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdauSerializerTransformer extends AbstractTransformer {

    private String outputFile;

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityApplianceDefinition;
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        IdApplianceProject prj = event.getContext().getProject();

        IdProjectModule module = prj.getRootModule();

        for (IdProjectResource r : module.getResources()) {

            if (r.getValue() instanceof Beans) {

                Beans beans = (Beans) r.getValue();

                try {


                    JAXBContext ctx = JAXBContext.newInstance("com.atricore.idbus.console.lifecycle.support.springmetadata.model:" +
                            "com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi:" +
                            "com.atricore.idbus.console.lifecycle.support.springmetadata.model.tool");
                    Marshaller m = ctx.createMarshaller();

                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("target/" + r.getName() + ".xml"));
                    XmlApplicationContextEnhancer x = new XmlApplicationContextEnhancer(writer);
                    m.marshal(beans, x);
                    x.flush();
                    writer.flush();
                    writer.close();


                } catch (FileNotFoundException e) {
                    throw new TransformException(e.getMessage(), e);
                } catch (JAXBException e) {
                    throw new TransformException(e.getMessage(), e);
                } catch (XMLStreamException e) {
                    throw new TransformException(e.getMessage(), e);
                } catch (IOException e) {
                    throw new TransformException(e.getMessage(), e);
                }

            }
        }


        return null;
    }
}
