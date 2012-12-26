package org.atricore.idbus.kernel.main.mediation.channel;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.provider.StatefulProvider;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 12/19/12
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface StatefulChannel extends Channel {

    StatefulProvider getProvider();
}
