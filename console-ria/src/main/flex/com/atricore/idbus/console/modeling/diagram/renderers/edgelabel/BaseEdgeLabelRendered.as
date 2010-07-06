/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
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

package com.atricore.idbus.console.modeling.diagram.renderers.edgelabel {

// Flash classes

import flash.events.Event;

import mx.controls.Label;
import mx.controls.LinkButton;

import org.un.cava.birdeye.ravis.components.renderers.BaseRenderer;
import org.un.cava.birdeye.ravis.utils.events.VGraphRendererEvent;

/**
 * This is an extension to the base renderer
 * specific to Nodes, i.e. it populates various
 * label fields and icons with node XML data.
 * */
public class BaseEdgeLabelRendered extends BaseRenderer {

    private static const _LOG:String = "components.renderers.edgeLabels.BaseEdgeLabelRenderer";

    /**
     * Base Constructor
     * */
    public function BaseEdgeLabelRendered() {
        super();
    }

    /**
     * @inheritDoc
     * */
    override protected function getDetails(e:Event):void {

        // LogUtil.debug(_LOG, "Show Details");
        var vgre:VGraphRendererEvent = new VGraphRendererEvent(VGraphRendererEvent.VG_RENDERER_SELECTED);

        /* do the checks in the super class */
        super.getDetails(e);

        /* prepare the event */

        /* make sure we have the XML attribute */
       /* if(this.data.data.@edgeLabel != null) {
            vgre.rname = this.data.data.@edgeLabel;
        } else {
            LogUtil.info(_LOG, "XML data object has no 'edgeLabel' attribute");
        } */

        /* now the description */
        /*if(this.data.data.@edgeDescription != null) {
            vgre.rdesc = this.data.data.@edgeDescription;
        } else {
            LogUtil.info(_LOG, "XML data object has no 'edgeDescription' attribute");
        }
		*/
        this.dispatchEvent(vgre);
    }

    /**
     * @inheritDoc
     *
     * Make sure we call the init methods of THIS
     * class and not the base class.
     * */
    override protected function initComponent(e:Event):void {

        /* initialize the upper part of the renderer */
        initTopPart();

        /* in between could be other components added
         * like images */

        /* now the link button */
        initLinkButton();

        /* or the label both would be overkill */
        //initLabel();
    }

    /**
     * @inheritDoc
     * */
    override protected function initTopPart():void {

        /* create the top part using super class */
        super.initTopPart();

        /* set the tool tip to be the name attribute of the XML object
         * we should also check here about the correctness of all
         * the data objects, just as in getDetails(), even more important
         * since this will be called earlier....
         * maybe it is all not so critical... anyway, we don't abort,
         * so just some diagnostic messages are printed... */
        /*if(this.data.data.@name != null) {
            this.toolTip = this.data.data.@edgeLabel;
        } else {
            LogUtil.info(_LOG, "XML data object has no 'edgeLabel' attribute");
        } */
    }

    /**
     * @inheritDoc
     * */
    override protected function initLinkButton():LinkButton {

        /* create the linkbutton using
         * the super class */
        super.initLinkButton();

        /* add node specific data;
         * here no check, but should be done */
        //lb.label = this.data.data.@edgeLabel;

        return lb;
    }

    /**
     * @inheritDoc
     * */
    override protected function initLabel():Label {

        /* base init */
        super.initLabel();

        /* again we should do checks here */
        //ll.text = this.data.data.@edgeLabel;

        return ll;
    }
}
}

