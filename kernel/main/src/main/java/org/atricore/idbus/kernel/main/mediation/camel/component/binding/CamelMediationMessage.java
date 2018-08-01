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
import org.apache.camel.impl.DefaultMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class CamelMediationMessage extends DefaultMessage {

    private static final Log logger = LogFactory.getLog(CamelMediationMessage.class);

    private MediationMessage message;

    @Override
    public DefaultMessage newInstance() {
        return new CamelMediationMessage();
    }

    @Override
    public void copyFrom(Message that) {

        logger.debug("Copy CamelMediationMessage from : " + that.getMessageId());
        setMessageId(that.getMessageId());
        //setBody(that.getBody());
        getHeaders().putAll(that.getHeaders());
        getAttachments().putAll(that.getAttachments());

        if (logger.isDebugEnabled()) {

            for(String key : that.getHeaders().keySet()) {
                logger.debug("copyFrom header "+key+":"+that.getHeader(key));
            }

            for(String key : getHeaders().keySet()) {
                logger.debug("copyFrom to-header "+key+":"+getHeader(key));
            }

        }

        // This is also done in the set body method!
        if (that instanceof CamelMediationMessage) {
            CamelMediationMessage orig = (CamelMediationMessage) that;
            setMessage(orig.getMessage());
            setBody(orig.getMessage());
        } else {
            setBody(that.getBody());
        }
    }

    @Override
    public Message copy() {
        logger.debug("Copy SamlR2 Message " + getMessageId());
        CamelMediationMessage copy = (CamelMediationMessage) super.copy();
        copy.setMessage(getMessage());
        return copy;
    }

    @Override
    protected Object createBody() {

        try {
            logger.debug("Create Mediation Message body " + getMessageId());
            Exchange ex = getExchange();
            CamelMediationEndpoint en = (CamelMediationEndpoint) ex.getFromEndpoint(); // TODO UPD_15
            return en.createBody(this);
        } catch (Exception e) {
            logger.error("Cannot create mediation message body : " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void setBody(Object body) {

        if (logger.isDebugEnabled())
            logger.debug("Setting body " + (body != null ? body.getClass().getName() : "<NULL>") + " in message " + getMessageId());

        super.setBody(body);
        if (body instanceof MediationMessage) {
            logger.debug("Setting message " + body.getClass().getName());
            this.message = (MediationMessage) body;
        }    
    }

    public MediationMessage getMessage() {
        return message;
    }

    public void setMessage(MediationMessage message) {
        setBody(message);
    }


}
