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

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.Transformer;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractTransformer implements Transformer {

    private static final String[] javaKeywordsConstants = {
            "abstract",
            "assert",
            "boolean",
            "byte",
            "case",
            "catch",
            "char",
            "class",
            "const",
            "continue",
            "default",
            "do",
            "double",
            "else",
            "enum",
            "extends",
            "final",
            "finally",
            "float",
            "for",
            "goto",
            "if",
            "implements",
            "import",
            "instanceof",
            "int",
            "interface",
            "long",
            "native",
            "new",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "short",
            "static",
            "strictfp",
            "super",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "try",
            "void",
            "volatile",
            "while",
    };
    
    private static final Set javaKeywords = new HashSet();
    static {
        javaKeywords.addAll(Arrays.asList(javaKeywordsConstants));
    }

    protected UUIDGenerator idGen = new UUIDGenerator();

    public boolean accept(TransformEvent event) {
        return false;
    }

    public void before(TransformEvent event) throws TransformException {

    }

    public Object after(TransformEvent event) throws TransformException {
        return null;
    }

    protected void resolveResource(Resource resource) throws IOException {

        InputStream is = getClass().getResourceAsStream(resource.getUri());
        if (is != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
                byte[] buff = new byte[4096];
                int read = is.read(buff, 0, 4096);
                while (read > 0) {
                    baos.write(buff, 0, read);
                    read = is.read(buff, 0, 4096);
                }
                resource.setValue(baos.toByteArray());

            } finally {
                if (is != null) try {
                    is.close();
                } catch (IOException e) {/**/}
            }
        }

    }

    // -----------------------------------------------------------------------
    // UI Location Utilities 
    // -----------------------------------------------------------------------

    protected String resolveUiLocationPath(IdentityAppliance appliance) {
        IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();
        if (applianceDef.getUiLocation() == null) {
            return "/IDBUS-UI/" + appliance.getName().toUpperCase();
        }
        return resolveLocationPath(applianceDef.getUiLocation());
    }

    protected String resolveUiErrorLocation(IdentityAppliance appliance) {
        return resolveLocationBaseUrl(appliance.getIdApplianceDefinition().getLocation()) + "/IDBUS-UI/" + appliance.getName().toUpperCase() + "/SSO/ERROR";
    }

    protected String resolveUiWarningLocation(IdentityAppliance appliance) {
        return resolveLocationBaseUrl(appliance.getIdApplianceDefinition().getLocation()) + "/IDBUS-UI/" + appliance.getName().toUpperCase() + "/SSO/WARN/POLICY-ENFORCEMENT";
    }

    // -----------------------------------------------------------------------
    // Location Utilities
    // -----------------------------------------------------------------------

    protected String resolveLocationUrl(Provider provider, Channel channel) {

        if (channel == null)
            return resolveLocationUrl(provider);

        Location cl = channel.getLocation();
        Location pl = provider.getLocation();
        Location al = provider.getIdentityAppliance().getLocation();

        String location = "";

        if (cl != null)
            location = resolveLocationUrl(cl);

        if (!"".equals(location) && !location.startsWith("/"))
            return location;

        if (pl != null)
            location = resolveLocationUrl(pl) + location;

        if (!"".equals(location) && !location.startsWith("/"))
            return location;

        if (al != null)
            location = resolveLocationUrl(al) + location;

        return location;

    }

    protected String resolveLocationUrl(Provider provider) {

        Location pl = provider.getLocation();
        Location al = provider.getIdentityAppliance().getLocation();

        String location = "";

        if (pl != null)
            location = resolveLocationUrl(pl);

        if (!"".equals(location) && !location.startsWith("/"))
            return location;

        if (al != null)
            location = resolveLocationUrl(al) + location;

        return location;
    }

    protected String resolveLocationBaseUrl(Provider provider) {
        if (provider.getLocation() == null) {
            return "";
        }
        // TODO : Location al = provider.getApplinaceDefinition().getLocation();
        Location l = provider.getLocation();
        return resolveLocationBaseUrl(l);
    }

    protected String resolveLocationPath(Location location) {
        if (location == null) {
            return "";
        }

        String contextString = "";
        if (location.getContext() != null && !location.getContext().equals("")) {
            contextString = (location.getContext().startsWith("/") ? location.getContext().substring(1) : location.getContext());
            contextString = (contextString.endsWith("/") ? contextString.substring(0, contextString.length() - 1) : contextString);
        }


        String uriString = "";
        if (location.getUri() != null && !location.getUri().equals("")) {

            uriString = location.getUri() != null ? location.getUri() : "";

            if (uriString.startsWith("/"))
                uriString = uriString.substring(1);

            if (uriString.endsWith("/"))
                uriString = uriString.substring(0, uriString.length() - 1);

            return "/" + contextString + "/" + uriString;
        } else {

            return "/" + contextString;
        }

    }

    protected String resolveLocationUrl(Location location) {
        if (location == null) {
            return "";
        }

        String path = resolveLocationPath(location);

        return resolveLocationBaseUrl(location) + path;
    }

    protected String resolveLocationBaseUrl(Location location) {

        if (location == null) {
            return "";
        }

        String portString = "";
        if (location.getPort() > 0)
            portString = location.getPort() + "";

        String protocolString = "";
        if (location.getProtocol() != null) {
            protocolString = location.getProtocol() + "://";
            // For HTTP, remove default ports
            if (location.getProtocol().equalsIgnoreCase("http"))
                portString = (location.getPort() == 80 ? "" : ":" + location.getPort());
            if (location.getProtocol().equalsIgnoreCase("https"))
                portString = (location.getPort() == 443 ? "" : ":" + location.getPort());
        }

        String hostString = "";
        if (location.getHost() != null)
            hostString = location.getHost();

        return protocolString + hostString + portString;
    }

    protected String normalizeBeanName(String name) {
        String regex = "[ .]";
        return name.replaceAll(regex, "-").toLowerCase();
    }

    protected String toPackageName(String namespace) {

        namespace = namespace.replace(':', '.');
        namespace = namespace.replace('/', '.');

        // Now, some Java specific issues: packages cannot be named after primitives:
        String pkg = null;
        StringTokenizer st = new StringTokenizer(namespace, ".");
        while (st.hasMoreTokens()) {
            String pkgName = st.nextToken();

            pkgName = toJavaPackageName(pkgName);
            if (pkg == null)
                pkg = pkgName;
            else
                pkg += "." + pkgName;
        }

        return pkg;
    }

    protected String toJavaPackageName(String singlePackageName) {

        // If it starts with a number
        
        try {
            Integer.parseInt(singlePackageName.substring(0, 1));
            return "_" + singlePackageName;
        } catch (NumberFormatException e) {
            // We're ok
        }

        // If is a java keyword!
        if (javaKeywords.contains(singlePackageName))
            return "_" + singlePackageName;
        
        return singlePackageName;

    }

    protected String toFolderName(String namespace) {
        namespace = namespace.replace(':', '/');
        namespace = namespace.replace('.', '/');
        return namespace;
    }


}
