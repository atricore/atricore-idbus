package com.atricore.idbus.console.services.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import flex.messaging.log.AbstractTarget;
import flex.messaging.log.LogEvent;

/**
 * TODO : Find a better place for this class
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class CommonsLoggingTarget extends AbstractTarget {
    // log4j levels:   OFF - FATAL - ERROR - WARN - INFO - DEBUG - TRACE - ALL
    // blazeds levels:  NONE - FATAL - ERROR - WARN - INFO - DEBUG - ALL
    
    public void logEvent(LogEvent event) {

        Log logger = LogFactory.getLog(event.logger.getCategory());

        if (event.level >= LogEvent.ERROR)
            logger.error(event.message, event.throwable);

        else if (event.level >= LogEvent.WARN)
            logger.warn(event.message, event.throwable);

        else if (event.level >= LogEvent.INFO)
             logger.info(event.message, event.throwable);

        else if (event.level >= LogEvent.DEBUG)
             logger.debug(event.message, event.throwable);

        else
             logger.trace(event.message, event.throwable);
    }
}
