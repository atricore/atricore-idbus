package com.atricore.idbus.console.config.main.model {
import com.atricore.idbus.console.services.dto.settings.HttpServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.LogServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.ManagementServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.PersistenceServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.SshServiceConfiguration;

import org.osmf.traits.IDisposable;
import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

public class ServiceConfigProxy extends IocProxy implements IDisposable {

    private var _httpService:HttpServiceConfiguration;

    private var _sshService:SshServiceConfiguration;

    private var _persistenceService:PersistenceServiceConfiguration;

    private var _managementService:ManagementServiceConfiguration;

    private var _logService:LogServiceConfiguration;

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

    public function get logService():LogServiceConfiguration {
        return _logService;
    }

    public function set logService(value:LogServiceConfiguration):void {
        _logService = value;
    }

    public function dispose():void {
        httpService = null;
    }
}
}