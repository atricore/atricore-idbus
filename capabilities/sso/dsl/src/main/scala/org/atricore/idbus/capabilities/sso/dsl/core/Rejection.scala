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

package org.atricore.idbus.capabilities.sso.dsl.core

/**
 * A rejection encapsulates a specific reason why an identity and access management route was not able to handle a request.
 * Rejections are gathered up over the course of an identity flow route evaluation and finally converted to
 * Responses.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait Rejection

case object NoStateAvailable extends Rejection
case object NoSecurityContextAvailable extends Rejection
case object NoSessionManagerAvailable extends Rejection
case object SessionNotAvailable extends Rejection
case object InvalidSession extends Rejection
case object SessionExists extends Rejection
case object NoMoreClaimEndpoints extends Rejection
case object NoAuthenticationStateAvailable extends Rejection
case object NoAuthenticationRequestAvailable extends Rejection
case object NoMoreIdentityConfirmationEndpoints extends Rejection

