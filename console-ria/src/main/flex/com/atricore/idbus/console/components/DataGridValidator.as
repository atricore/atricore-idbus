package com.atricore.idbus.console.components {
import mx.collections.ArrayCollection;
import mx.validators.ValidationResult;
import mx.validators.Validator;

public class DataGridValidator extends Validator {

    public function DataGridValidator() {
        super();
    }

    override protected function doValidation(value:Object):Array {

        var results:Array = [];
        results = super.doValidation(value);

        // Return if there are errors.
        if (results.length > 0)
            return results;

        if (required && ((value as ArrayCollection) == null || (value as ArrayCollection).length == 0)) {
            results.push(new ValidationResult(true, null, "dataGridEmpty",
                    resourceManager.getString(AtricoreConsole.BUNDLE, 'error.dataGrid.empty')));
            return results;

        }
        return results;

    }
}
}