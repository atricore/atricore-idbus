/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.main.controller {
import com.atricore.idbus.console.main.service.ServiceRegistry;

public class StartupContext {

    private var _registry:ServiceRegistry;

    private var _app:AtricoreConsole;

    public function StartupContext(app:AtricoreConsole, serviceRegistry:ServiceRegistry) {
        this._registry = registry;
        this._app = app;
    }


    public function get registry():ServiceRegistry {
        return _registry;
    }


    public function get app():AtricoreConsole {
        return _app;
    }
}
}
