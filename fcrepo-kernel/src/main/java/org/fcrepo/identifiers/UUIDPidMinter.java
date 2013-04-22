
package org.fcrepo.identifiers;

import static com.yammer.metrics.MetricRegistry.name;
import static java.util.UUID.randomUUID;
import static org.fcrepo.services.RepositoryService.metrics;

import com.yammer.metrics.Timer;

/**
 * Simple PidMinter that replies on Java's inbuilt UUID minting.
 * 
 * @author ajs6f
 *
 */
public class UUIDPidMinter extends BasePidMinter {

    static final Timer timer = metrics.timer(name(UUIDPidMinter.class, "mint"));

    /**
     * Mint a unique identifier as a UUID
     * @return
     */
    @Override
    public String mintPid() {

        final Timer.Context context = timer.time();

        try {
            return randomUUID().toString();
        } finally {
            context.stop();
        }
    }

}
