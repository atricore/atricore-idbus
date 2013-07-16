package org.atricore.idbus.idojos.virtualidentitystore.rule;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.idojos.virtualidentitystore.BindMappingRule;
import org.atricore.idbus.idojos.virtualidentitystore.BindOutcome;
import org.atricore.idbus.idojos.virtualidentitystore.BaseBindMappingRule;

/**
 * Returns successful bind outcome if binding was successful 
 * on any source.
 * 
 * @org.apache.xbean.XBean element="bind-on-any-source"
 */
public class SuccessfulBindOnAnySource extends BaseBindMappingRule implements BindMappingRule {

    private static final Log logger = LogFactory.getLog(SuccessfulBindOnAnySource.class);

    public BindOutcome join(Collection<BindOutcome> selectedOutcomes) {
    	BindOutcome jointOutcome = null;
        for (Iterator<BindOutcome> selectedOutcomesIterator = selectedOutcomes.iterator(); selectedOutcomesIterator.hasNext();) {
            BindOutcome selectedOutcome = selectedOutcomesIterator.next();
            if (selectedOutcome.isSuccessful()) {
            	jointOutcome = selectedOutcome;
            	break;
            }
        }

        if (jointOutcome == null && selectedOutcomes != null && 
        		selectedOutcomes.size() > 0) {
        	jointOutcome = selectedOutcomes.iterator().next();
        }
        
        return jointOutcome;
    }


}