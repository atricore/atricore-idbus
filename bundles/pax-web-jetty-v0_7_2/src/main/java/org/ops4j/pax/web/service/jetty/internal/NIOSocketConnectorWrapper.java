/* Copyright 2007 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.service.jetty.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.nio.SelectChannelConnector;

/**
 * Wraps a jetty SocketConnector in order to catch exceptions on connector opening.
 * If that's the case it will just log the
 *
 * @author Matthew Roy
 * @since 0.5.1, July 24, 2008
 */
class NIOSocketConnectorWrapper
        extends SelectChannelConnector
{

    private static final Log LOG = LogFactory.getLog( NIOSocketConnectorWrapper.class  );

    protected void doStart()
            throws Exception
    {
        try
        {
            super.doStart();
        }
        catch( Exception e )
        {
            LOG.warn( "Connection on port " + getPort() + " cannot be open. Reason: " + e.getMessage() );
        }

    }

}
