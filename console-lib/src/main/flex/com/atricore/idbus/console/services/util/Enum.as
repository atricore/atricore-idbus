/*
  GRANITE DATA SERVICES
  Copyright (C) 2007-2008 ADEQUATE SYSTEMS SARL

  This file is part of Granite Data Services.

  Granite Data Services is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or (at your
  option) any later version.

  Granite Data Services is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
  for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.
*/

package com.atricore.idbus.console.services.util {

    import flash.utils.IDataInput;
    import flash.utils.getQualifiedClassName;

    /**
	 * 	ActionScript implementation of an Enum class
	 *
     * 	@author Franck WOLFF
     */
    public class Enum {

        private var _name:String;


        function Enum(name:String, restrictor:*) {
            _name = (restrictor is Restrictor ? name : constantOf(name).name);
        }

        public function set name(value:String):void {
            _name = constantOf(value).name
        }

        public function get name():String {
            return _name;
        }

        protected function getConstants():Array {
            throw new Error("Should be overriden");
        }

        protected function constantOf(name:String):Enum {
            for each (var o:* in getConstants()) {
                var enum:Enum = Enum(o);
                if (enum.name == name)
                    return enum;
            }
            throw new ArgumentError("Invalid " + getQualifiedClassName(this) + " value: " + name);
        }
        
        public static function readEnum(input:IDataInput):Enum {
            var tmp:Enum = input.readObject() as Enum;
            return (tmp == null ? null : tmp.constantOf(tmp.name));
        }

        public function toString():String {
            return name;
        }

        public function equals(other:Enum):Boolean {
        	return other === this || (
        		other != null &&
        		getQualifiedClassName(this) == getQualifiedClassName(other) &&
        		other.name == this.name
        	);
        }
                                                            

        protected static function get _():Restrictor {
            return new Restrictor();
        }
    }
}
class Restrictor {}