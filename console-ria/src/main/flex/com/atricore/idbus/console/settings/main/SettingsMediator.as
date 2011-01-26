/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main {
import com.atricore.idbus.console.base.extensions.appsection.AppSectionMediator;
import com.atricore.idbus.console.main.model.ProjectProxy;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;

public class SettingsMediator extends AppSectionMediator implements IDisposable {


    public function SettingsMediator() {
    }

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _projectProxy:ProjectProxy;

    public function dispose():void {

    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    // TODO : Discover

}

}
