
package org.fcrepo.integration;

import static org.slf4j.LoggerFactory.getLogger;

import org.fcrepo.services.RepositoryService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractIT {

    protected Logger logger;

    @Before
    public void setLogger() {
        logger = getLogger(this.getClass());
    }

    @AfterClass
    public static void dumpMetrics() {
        RepositoryService.dumpMetrics(System.out);
    }

}
