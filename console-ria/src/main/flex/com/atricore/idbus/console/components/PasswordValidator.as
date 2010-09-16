package com.atricore.idbus.console.components {
import mx.validators.ValidationResult;
import mx.validators.Validator;

public class PasswordValidator extends Validator
{
    // Define Array for the return value of doValidation().
    private var results:Array;

    public function PasswordValidator()
    {
        super();
    }

    public var confirmationSource: Object;
    public var confirmationProperty: String;

    override protected function doValidation(value:Object):Array {

        // Call base class doValidation().
        var results:Array = super.doValidation(value.password);

        if (value.password != value.confirmation) {
            results.push(new ValidationResult(true, null, "Mismatch",
                    "Password Dosen't match Retype!"));
        }

        return results;
    }

    /**
     *  @private
     *  Grabs the data for the confirmation password from its different sources
     *  if its there and bundles it to be processed by the doValidation routine.
     */
    override protected function getValueFromSource():Object
    {
        var value:Object = {};

        value.password = super.getValueFromSource();

        if (confirmationSource && confirmationProperty)
        {
            value.confirmation = confirmationSource[confirmationProperty];
        }

        return  value;
    }
}
}
