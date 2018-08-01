/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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

package org.atricore.idbus.kernel.main.mediation;



/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Rev: 1359 $ $Date: 2009-07-19 13:57:57 -0300 (Sun, 19 Jul 2009) $
 */
public interface MessageQueueManager  {

    public String getName();

    public void init() throws Exception ;

    public Object pullMessage(Artifact artifact) throws Exception ;

    public Object peekMessage(Artifact artifact) throws Exception ;

    public Artifact pushMessage(Object content) throws Exception ;

    public void shutDown() throws Exception ;

    public ArtifactGenerator getArtifactGenerator() ;


}