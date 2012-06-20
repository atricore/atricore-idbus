package com.atricore.idbus.console.config.main.model {
import com.atricore.idbus.console.services.dto.settings.ArtifactQueueManagerConfiguration;
import com.atricore.idbus.console.services.dto.settings.HttpServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.LogServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.ManagementServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.PersistenceServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.SshServiceConfiguration;

import mx.collections.ArrayCollection;

import org.osmf.traits.IDisposable;
import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

public class ServiceConfigProxy extends IocProxy implements IDisposable {

    private var _httpService:HttpServiceConfiguration;

    private var _sshService:SshServiceConfiguration;

    private var _persistenceService:PersistenceServiceConfiguration;

    private var _managementService:ManagementServiceConfiguration;

    private var _artifactQueueManagerService:ArtifactQueueManagerConfiguration;

    private var _logService:LogServiceConfiguration;

    private var _brandingDefinitions:ArrayCollection;

    public function ServiceConfigProxy() {
        super(NAME);
    }

    public function get httpService():HttpServiceConfiguration {
        return _httpService;
    }

    public function set httpService(value:HttpServiceConfiguration):void {
        _httpService = value;
    }

    public function get sshService():SshServiceConfiguration {
        return _sshService;
    }

    public function set sshService(value:SshServiceConfiguration):void {
        _sshService = value;
    }

    public function get persistenceService():PersistenceServiceConfiguration {
        return _persistenceService;
    }

    public function set persistenceService(value:PersistenceServiceConfiguration):void {
        _persistenceService = value;
    }

    public function get managementService():ManagementServiceConfiguration {
        return _managementService;
    }

    public function set managementService(value:ManagementServiceConfiguration):void {
        _managementService = value;
    }

    public function get artifactQueueManagerService():ArtifactQueueManagerConfiguration {
        return _artifactQueueManagerService;
    }

    public function set artifactQueueManagerService(value:ArtifactQueueManagerConfiguration):void {
        _artifactQueueManagerService = value;
    }

    public function get logService():LogServiceConfiguration {
        return _logService;
    }

    public function set logService(value:LogServiceConfiguration):void {
        _logService = value;
    }

    public function get brandingDefinitions():ArrayCollection {
        return _brandingDefinitions;
    }

    public function set brandingDefinitions(value:ArrayCollection):void {
        _brandingDefinitions = value;
    }

    public function dispose():void {
        httpService = null;
    }
}
}