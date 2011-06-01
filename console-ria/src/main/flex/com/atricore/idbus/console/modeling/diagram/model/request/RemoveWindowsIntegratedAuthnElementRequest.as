/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.WindowsIntegratedAuthentication;

public class RemoveWindowsIntegratedAuthnElementRequest {
    private var _windowsIntegratedAuthentication:WindowsIntegratedAuthentication;
    public function RemoveWindowsIntegratedAuthnElementRequest(windowsIntegratedAuthentication:WindowsIntegratedAuthentication) {
        _windowsIntegratedAuthentication = windowsIntegratedAuthentication;
    }

    public function get windowsIntegratedAuthentication():WindowsIntegratedAuthentication {
        return _windowsIntegratedAuthentication;
    }
}
}
