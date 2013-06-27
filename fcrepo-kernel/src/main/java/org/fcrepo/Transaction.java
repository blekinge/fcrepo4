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

package org.fcrepo;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class Transaction {

    // the default timeout is 3 minutes
    public static final long DEFAULT_TIMEOUT = 3l * 60l * 1000l;

    public static final String TIMEOUT_SYSTEM_PROPERTY = "fcrepo4.tx.timeout";

    /**
     * @todo Add Documentation.
     */
    public static enum State {
        DIRTY, NEW, COMMITED, ROLLED_BACK;
    }

    private final Session session;

    private final String id;

    private final Date created;

    private Calendar expires;

    private State state = State.NEW;

    private Transaction() {
        this.session = null;
        this.created = null;
        this.id = null;
        this.expires = null;
    }

    /**
     * @todo Add Documentation.
     */
    public Transaction(Session session) {
        super();
        this.session = session;
        this.created = new Date();
        this.id = UUID.randomUUID().toString();
        this.expires = Calendar.getInstance();
        this.updateExpiryDate();
    }

    /**
     * @todo Add Documentation.
     */
    public Session getSession() {
        updateExpiryDate();
        return TxAwareSession.newInstance(session, id);
    }

    /**
     * @todo Add Documentation.
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @todo Add Documentation.
     */
    public String getId() {
        return id;
    }

    /**
     * @todo Add Documentation.
     */
    public State getState() throws RepositoryException {
        if (this.session != null && this.session.hasPendingChanges()) {
            return State.DIRTY;
        }
        return state;
    }

    /**
     * @todo Add Documentation.
     */
    public Date getExpires() {
        return expires.getTime();
    }

    /**
     * @todo Add Documentation.
     */
    public void commit() throws RepositoryException {
        this.session.save();
        this.state = State.COMMITED;
        this.expire();
    }

    /**
     * End the session, and mark for reaping
     * @throws RepositoryException
     */
    public void expire() throws RepositoryException {
        this.session.logout();
        this.expires.setTimeInMillis(System.currentTimeMillis());
    }

    /**
     * Discard all unpersisted changes and expire
     * @throws RepositoryException
     */
    public void rollback() throws RepositoryException {
        this.state = State.ROLLED_BACK;
        this.session.refresh(false);
        this.expire();
    }

    /**
     * Roll forward the expiration date for recent activity
     */
    public void updateExpiryDate() {
        long duration;
        if (System.getProperty(TIMEOUT_SYSTEM_PROPERTY) != null) {
            duration =
                    Long.parseLong(System.getProperty(TIMEOUT_SYSTEM_PROPERTY));
        } else {
            duration = DEFAULT_TIMEOUT;
        }
        this.expires.setTimeInMillis(System.currentTimeMillis() + duration);
    }
}
