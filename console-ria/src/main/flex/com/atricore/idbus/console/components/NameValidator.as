package com.atricore.idbus.console.components {
import mx.validators.ValidationResult;
import mx.validators.Validator;

public class NameValidator extends Validator {

    public function NameValidator() {
        super();
    }

    override protected function doValidation(value:Object):Array {
        var results:Array = [];
        results = super.doValidation(value);

        // Return if there are errors.
        if (results.length > 0)
            return results;

        var pattern:RegExp = new RegExp("^[A-Za-z\\d-]+$");

        var patternResult:Object = pattern.exec(String(value));        
        if ((required && value == null) || (value != null && patternResult == null)) {
            results.push(new ValidationResult(true, null, "notNamespace", resourceManager.getString(AtricoreConsole.BUNDLE, 'error.name.invalid')));
            return results;

        }
        return results;
    }
}
}