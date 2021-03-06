/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.identifiers;

import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @todo Add Documentation.
 * @author Chris Beer
 * @date Mar 14, 2013
 */
public class UUIDPidMinterTest {

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testMintPid() throws Exception {

        final PidMinter pidMinter = new UUIDPidMinter();

        final String pid = pidMinter.mintPid();

        assertTrue("PID wasn't a UUID", compile(
                "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
                .matcher(pid).find());

    }
}
