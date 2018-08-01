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

package org.atricore.idbus.kernel.main.mediation.camel.component.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.message.MessageContentsList;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractMediationSoapBinding extends AbstractMediationBinding {

    private static final Log logger = LogFactory.getLog(AbstractMediationSoapBinding.class);

    protected AbstractMediationSoapBinding(String binding, Channel channel) {
        super(binding, channel);
    }

    public void copyFaultMessageToExchange(CamelMediationMessage fault, Exchange exchange) {
        MediationMessage faultMessage = fault.getMessage();
        if (logger.isDebugEnabled())
            logger.debug("Copying Fault Message : " +
                    (faultMessage.getFault() != null ? faultMessage.getFault().getMessage() : "") + " " +
                    faultMessage.getFaultDetails(), faultMessage.getFault()
        );

        // TODO: This is probably wrong, we need a SOAP fault


        fault.setFault(true);
        exchange.getOut().setBody(fault);

    }

    public void copyMessageToExchange(CamelMediationMessage message, Exchange exchange) {
        if (logger.isDebugEnabled())
            logger.debug("Copying SOAP Message");

        MediationMessage outMsg = message.getMessage();
        MessageContentsList mclOut = new MessageContentsList();
        mclOut.set(0, outMsg.getContent());

        copyBackState(outMsg.getState(), exchange);

        exchange.getOut().setBody(mclOut, MessageContentsList.class);

    }

    protected int getRetryCount() {
        String retryCountStr = getConfigurationContext().getProperty("binding.soap.loadStateRetryCount");
        if (retryCountStr == null)
            return -1;

        int retryCount = Integer.parseInt(retryCountStr);
        if (retryCount < 1) {
            logger.warn("Configuratio property 'binding.soap.loadStateRetryCount' cannot be " + retryCount);
            retryCount = 3;
        }

        return retryCount;
    }

    protected long getRetryDelay() {
        String retryDelayStr = getConfigurationContext().getProperty("binding.soap.loadStateRetryDelay");
        if (retryDelayStr == null)
            return -1;

        long retryDelay = Long.parseLong(retryDelayStr);
        if (retryDelay < 0) {
            logger.warn("Configuratio property 'binding.soap.loadStateRetryDelay' cannot be " + retryDelay);
            retryDelay = 100;
        }

        return retryDelay;

    }


}
