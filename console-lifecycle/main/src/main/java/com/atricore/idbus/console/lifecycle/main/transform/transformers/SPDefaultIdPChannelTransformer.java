package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.ServiceProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SPDefaultIdPChannelTransformer extends AbstractIdPChannelTransformer {

    private static final Log logger = LogFactory.getLog(SPDefaultIdPChannelTransformer.class);

    public SPDefaultIdPChannelTransformer() {
        super();
        setContextIdpChannelBean("defaultIdpChannelBean");
    }
    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof ServiceProvider &&
                !((ServiceProvider)event.getData()).isRemote();
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        ServiceProvider sp = (ServiceProvider) event.getData();
        generateSPComponents(sp, null, null, null, null, event.getContext());
    }


}

