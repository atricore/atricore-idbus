package com.atricore.idbus.console.components {
import mx.validators.ValidationResult;
import mx.validators.Validator;

public class NamespaceValidator extends Validator {

    public function NamespaceValidator() {
        super();
    }

    override protected function doValidation(value:Object):Array {
        var results:Array = [];
        results = super.doValidation(value);

        // Return if there are errors.
        if (results.length > 0)
            return results;

        var pattern:RegExp = new RegExp("^[a-z\\d]([a-z\\d](.?))*[a-z\\d]$");

        var patternResult:Object = pattern.exec(String(value));
        // run the pattern, but don't error if there is no value and this is not required
        if ((required && value == null) || (value != null && patternResult == null)) {
            results.push(new ValidationResult(true, null, "notNamespace", resourceManager.getString(AtricoreConsole.BUNDLE, 'error.namespace.invalid')));
            return results;

        }
        return results;
    }
}
}