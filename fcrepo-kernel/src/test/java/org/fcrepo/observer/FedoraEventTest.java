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
package org.fcrepo.observer;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @todo Add Documentation.
 * @author Chris Beer
 * @date Mar 14, 2013
 */
public class FedoraEventTest {

    Event e = new FedoraEvent(new TestEvent(1, "Path", "UserId", "Identifier",
            ImmutableMap.of("1", "2"), "data", 0L));


    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testWrapNullEvent() throws Exception {
        new FedoraEvent(null);
    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testGetType() throws Exception {
        assertEquals(1, e.getType());
    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testGetPath() throws Exception {
        assertEquals("Path", e.getPath());

    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testGetUserID() throws Exception {

        assertEquals("UserId", e.getUserID());

    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testGetIdentifier() throws Exception {

        assertEquals("Identifier", e.getIdentifier());

    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testGetInfo() throws Exception {
        final Map<?, ?> m = e.getInfo();

        assertEquals("2", m.get("1"));
    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testGetUserData() throws Exception {

        assertEquals("data", e.getUserData());

    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testGetDate() throws Exception {
        assertEquals(0L, e.getDate());

    }

    /**
     * @todo Add Documentation.
     * @author
     * @date
     */
    class TestEvent implements Event {

        private final int type;

        private final String path;

        private final String user_id;

        private final String identifier;

        private final Map<String, String> info;

        private final String userData;

        private final long date;

        public TestEvent(final int type, final String path,
                final String user_id, final String identifier,
                final Map<String, String> info, final String userData,
                final long date) {
            this.type = type;
            this.path = path;
            this.user_id = user_id;
            this.identifier = identifier;
            this.info = info;
            this.userData = userData;
            this.date = date;
        }

        /**
         * @todo Add Documentation.
         */
        @Override
        public int getType() {
            return type;
        }

        /**
         * @todo Add Documentation.
         */
        @Override
        public String getPath() throws RepositoryException {
            return path;
        }

        /**
         * @todo Add Documentation.
         */
        @Override
        public String getUserID() {
            return user_id;
        }

        /**
         * @todo Add Documentation.
         */
        @Override
        public String getIdentifier() throws RepositoryException {
            return identifier;
        }

        /**
         * @todo Add Documentation.
         */
        @Override
        public Map<String, String> getInfo() throws RepositoryException {
            return info;
        }

        /**
         * @todo Add Documentation.
         */
        @Override
        public String getUserData() throws RepositoryException {
            return userData;
        }

        /**
         * @todo Add Documentation.
         */
        @Override
        public long getDate() throws RepositoryException {
            return date;
        }
    }
}
