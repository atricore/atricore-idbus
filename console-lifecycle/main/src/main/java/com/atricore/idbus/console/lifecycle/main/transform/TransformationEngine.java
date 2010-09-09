/*
 * Copyright (c) 2010., Atricore Inc.
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

package com.atricore.idbus.console.lifecycle.main.transform;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceDefinitionWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class TransformationEngine  {

    private static final Log logger = LogFactory.getLog(TransformationEngine.class);

    // TODO : Use spring DM (osgi export/import) to fill this!
    private List<Transformer> transformers = new ArrayList<Transformer>();

    private List<Cycle> cycles = new ArrayList<Cycle>();

    private IdentityApplianceDefinitionWalker walker = new ReflexiveIdentityApplianceDefinitionWalker();

    public IdentityApplianceDefinitionWalker getWalker() {
        return walker;
    }


    public void setWalker(IdentityApplianceDefinitionWalker walker) {
        this.walker = walker;
    }

    public List<Transformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    public List<Cycle> getCycles() {
        return cycles;
    }

    public void setCycles(List<Cycle> cycles) {
        this.cycles = cycles;
    }

    public IdApplianceTransformationContext transform(IdentityAppliance appliance, String cycleName) {
        IdApplianceTransformationContext ctx = doMakeContext(appliance);
        for (Cycle cycle : cycles) {
            if (cycle.getName().equals(cycleName)) {
                transform(appliance.getIdApplianceDefinition(), cycle, ctx);
                break;
            }
        }
        return ctx;

    }

    public IdApplianceTransformationContext transform(IdentityAppliance appliance) {

        IdApplianceTransformationContext ctx = doMakeContext(appliance);
        for (Cycle cycle : cycles) {
            transform(appliance.getIdApplianceDefinition(), cycle, ctx);
        }
        return ctx;

    }

    protected void transform(IdentityApplianceDefinition identityApplianceDefinition,
                             Cycle cycle,
                             IdApplianceTransformationContext ctx) {

        if (logger.isTraceEnabled())
            logger.trace("cycle         -> " + cycle.getName());

        ctx.setCurrentCycle(cycle);

        for (Phase phase : cycle.getPhases()) {

            if (logger.isTraceEnabled())
                logger.trace("phase         -> " + phase.getName());

            ctx.setCurrentPhase(phase);

            // Walk the entier tree for each phase.
            // Ideally, the output of a phase should be the input of the next but ... we are not arcsense :) so
            TransformerVisitor v = doMakeVisitor(ctx, phase);
            walker.walk(identityApplianceDefinition, v);

            ctx.setCurrentPhase(null);
        }


        ctx.setCurrentCycle(null);

    }

    protected List<Transformer> lookupTransformers(Cycle cycle, Phase phase) {
        return phase.getTransformers();
    }

    protected IdApplianceTransformationContext doMakeContext(IdentityAppliance appliance) {
        IdApplianceProject prj = new IdApplianceProject(
                appliance.getIdApplianceDefinition().getName().toLowerCase().replaceAll(" ", "-"),
                appliance.getIdApplianceDefinition().getDescription());

        prj.setDefinition(appliance.getIdApplianceDefinition());
        prj.setIdAppliance(appliance);

        return new IdApplianceTransformationContext(prj);
    }

    protected TransformerVisitor doMakeVisitor(IdApplianceTransformationContext ctx, Phase phase) {

        TransformerVisitor v = new TransformerVisitor();
        v.setContext(ctx);
        v.getTransformers().addAll(phase.getTransformers());
        return v;

    }

}
