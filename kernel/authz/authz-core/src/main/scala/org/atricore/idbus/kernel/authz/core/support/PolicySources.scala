/*
 * Atricore IDBus
 *
 * Copyright (c) 2009-2012, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.kernel.authz.core.support

import java.io.File
import java.net.URL
import io.Source
import org.atricore.idbus.kernel.authz.core.PolicySource
import org.atricore.idbus.kernel.authz.core.PolicySource
import org.atricore.idbus.kernel.authz.core.util._

class StringPolicySource(uri: String, text: String) extends StringResource(uri, text) with PolicySource

class UriPolicySource(uri: String, resourceLoader: ResourceLoader) extends UriResource(uri, resourceLoader) with PolicySource

class FilePolicySource(file: File, uri: String) extends FileResource(file, uri) with PolicySource

class URLPolicySource(url: URL) extends URLResource(url) with PolicySource

class SourcePolicySource(uri: String, source: Source) extends SourceResource(uri, source) with PolicySource

class CustomExtensionPolicySource(source: PolicySource, extensionName: String) extends DelegateResource with PolicySource {
  override def policyType = Some(extensionName)

  def delegate = source
}