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

package com.atricore.idbus.console.services.business {
import com.atricore.idbus.console.services.spi.request.SignOnRequest;

import com.atricore.idbus.console.services.spi.request.SignOutRequest;

import com.atricore.idbus.console.services.spi.request.UserLoggedRequest;

import mx.rpc.AsyncToken;

import mx.rpc.remoting.RemoteObject;

public class SignOnService implements ISignOnService{

        private var ro:RemoteObject;

        private static var _instance:SignOnService;

        public function SignOnService() {
            if (_instance != null)
                 throw new Error("Singleton can only be accessed through Singleton.instance");
            this.ro = new RemoteObject("signOnService");
            _instance = this;
        }

        public static function get instance():SignOnService {
            if (_instance == null)  _instance = new SignOnService();
                return _instance;
        }

        public function signOn(signOnRequest:SignOnRequest):AsyncToken {
            return ro.signOn(signOnRequest);
        }

        public function signOut(signOutRequest:SignOutRequest):AsyncToken {
            return ro.signOut(signOutRequest);
        }

        public function userLogged(userLoggedRequest:UserLoggedRequest):AsyncToken{
            return ro.userLogged(userLoggedRequest);
        }
    }
}