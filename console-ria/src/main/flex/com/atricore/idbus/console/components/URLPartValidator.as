/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.components {

import mx.validators.ValidationResult;
import mx.validators.Validator;

public class URLPartValidator extends Validator	{


        public function URLPartValidator() {
            super();
        }

        override protected function doValidation(value:Object):Array {

			var results:Array = [];
			results = super.doValidation(value);

			// Return if there are errors.
			if (results.length > 0)
				return results;

            /*"^(http(s?)://)(www.)?([A-Za-z0-9\\.\\-_]+)+([A-Za-z]{2,3})?(:[\\d]{1,5})?(/[A-Za-z0-9\\.\\-_]*)*"*/
			var pattern:RegExp = new RegExp("^(([\\w.-])*)*\\b$");

            var patternResult:Object = pattern.exec(String(value));
			// run the pattern, but don't error if there is no value and this is not required
			if ((required && value == null) || (value != null && patternResult == null)) {
				results.push(new ValidationResult(true, null, "notURL", resourceManager.getString(AtricoreConsole.BUNDLE, 'error.url.invalid')));
				return results;

			}
			return results;

		}
	}
}