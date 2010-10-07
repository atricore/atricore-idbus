package com.atricore.idbus.console.activation.main.impl;

import com.atricore.idbus.console.activation.main.exception.ActivationException;
import com.atricore.idbus.console.activation.main.spi.ActivationService;
import com.atricore.idbus.console.activation.main.spi.Activator;
import com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest;
import com.atricore.idbus.console.activation.main.spi.request.ActivateSamplesRequest;
import com.atricore.idbus.console.activation.main.spi.request.ConfigureAgentRequest;
import com.atricore.idbus.console.activation.main.spi.request.PlatformSupportedRequest;
import com.atricore.idbus.console.activation.main.spi.response.ActivateAgentResponse;
import com.atricore.idbus.console.activation.main.spi.response.ActivateSamplesResponse;
import com.atricore.idbus.console.activation.main.spi.response.ConfigureAgentResponse;
import com.atricore.idbus.console.activation.main.spi.response.PlatformSupportedResponse;
import org.josso.tooling.gshell.core.support.MessagePrinter;
import org.josso.tooling.gshell.install.installer.Installer;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;


/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JOSSOActivationServiceImpl implements ActivationService, InitializingBean {

    private List<Installer> installers;

    private MessagePrinter printer;

    private String jossoVersion;

    public void afterPropertiesSet() throws Exception {
        System.setProperty("josso-gsh.home", System.getProperty("karaf.base") + "/josso");
    }

    public PlatformSupportedResponse isSupported(PlatformSupportedRequest request) throws ActivationException {
        boolean supported = false;
        for (Installer i : installers) {
            if (i.getPlatformId().equals(request.getTargetPlatformId())) {
                supported = true;
                break;
            }
        }

        PlatformSupportedResponse response = new PlatformSupportedResponse();
        response.setSupported(supported);
        response.setPlatformId(request.getTargetPlatformId());

        return response;
    }

    public ActivateAgentResponse activateAgent(ActivateAgentRequest request) throws ActivationException {

        try {
            ActivateAgentResponse response = new ActivateAgentResponse ();
            Activator activator = new AgentActivator(installers, printer, request, response);

            activator.doActivate();



            return response;
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public ConfigureAgentResponse configureAgent(ConfigureAgentRequest request) throws ActivationException {
        try {
            ConfigureAgentResponse response = new ConfigureAgentResponse();
            Activator activator = new AgentConfigActivator(installers, printer, request, response);

            activator.doActivate();

            return response;
        } catch (Exception e) {
            throw new ActivationException(e);
        }

    }

    public ActivateSamplesResponse activateSamples(ActivateSamplesRequest request) throws ActivationException {
        try {
            ActivateSamplesResponse response = new ActivateSamplesResponse ();
            Activator activator = new SamplesActivator(installers, printer, request, response);

            activator.doActivate();

            return response;
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public List<Installer> getInstallers() {
        return installers;
    }

    public void setInstallers(List<Installer> installers) {
        this.installers = installers;
    }

    public MessagePrinter getPrinter() {
        return printer;
    }

    public void setPrinter(MessagePrinter printer) {
        this.printer = printer;
    }

    public String getJossoVersion() {
        return jossoVersion;
    }

    public void setJossoVersion(String jossoVersion) {
        this.jossoVersion = jossoVersion;
    }
}
