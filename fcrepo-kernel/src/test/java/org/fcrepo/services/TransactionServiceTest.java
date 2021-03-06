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

package org.fcrepo.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.fcrepo.Transaction;
import org.fcrepo.Transaction.State;
import org.fcrepo.exception.TransactionMissingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * @author frank asseg
 *
 */
public class TransactionServiceTest {
    private static final String IS_A_TX = "foo";
    private static final String NOT_A_TX = "bar";

    TransactionService service;

    Session mockSession;

    Transaction mockTx;

    @Before
    public void setup() throws Exception {
        service = new TransactionService();
        mockTx = mock(Transaction.class);
        when(mockTx.getId()).thenReturn(IS_A_TX);
        Field txsField = TransactionService.class.getDeclaredField("TRANSACTIONS");
        txsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Transaction> txs = (Map<String, Transaction>) txsField.get(TransactionService.class);
        txs.put(IS_A_TX, mockTx);
    }

    @Test
    public void testExpiration() throws Exception {
        Date fiveSecondsAgo = new Date(System.currentTimeMillis() - 5000);
        when(mockTx.getExpires()).thenReturn(fiveSecondsAgo);
        service.removeAndRollbackExpired();
        verify(mockTx).rollback();
    }


    @Test
    public void testExpirationThrowsRepositoryException() throws Exception {
        Date fiveSecondsAgo = new Date(System.currentTimeMillis() - 5000);
        Mockito.doThrow(new RepositoryException("")).when(mockTx).rollback();
        when(mockTx.getExpires()).thenReturn(fiveSecondsAgo);
        service.removeAndRollbackExpired();
    }

    @Test
    public void testCreateTx() throws Exception {
        Transaction tx = service.beginTransaction(null);
        assertNotNull(tx);
        assertNotNull(tx.getCreated());
        assertNotNull(tx.getId());
        assertEquals(State.NEW, tx.getState());
    }

    @Test
    public void testGetTx() throws Exception {
        Transaction tx = service.getTransaction(IS_A_TX);
        assertNotNull(tx);
    }

    @Test(expected = RepositoryException.class)
    public void testGetNonTx() throws TransactionMissingException {
        service.getTransaction(NOT_A_TX);

    }

    @Test
    public void testExists() throws Exception {
        assertTrue(service.exists(IS_A_TX));
        assertFalse(service.exists(NOT_A_TX));

    }

    @Test
    public void testCommitTx() throws Exception {
        Transaction tx = service.commit(IS_A_TX);

        assertNotNull(tx);
        verify(mockTx).commit();
    }

    @Test(expected =  RepositoryException.class)
    public void testCommitRemovedTransaction() throws Exception {
        Transaction tx = service.commit(IS_A_TX);
        service.getTransaction(tx.getId());
    }

    @Test
    public void testRollbackTx() throws Exception {
        Transaction tx = service.rollback(IS_A_TX);

        assertNotNull(tx);
        verify(mockTx).rollback();
    }

    @Test(expected =  RepositoryException.class)
    public void testRollbackRemovedTransaction() throws Exception {
        Transaction tx = service.rollback(IS_A_TX);
        service.getTransaction(tx.getId());
    }

    @Test(expected = RepositoryException.class)
    public void testRollbackWithNonTx() throws RepositoryException {
        Transaction tx = service.rollback(NOT_A_TX);
    }

    @Test(expected = RepositoryException.class)
    public void testCommitWithNonTx() throws RepositoryException {
        Transaction tx = service.commit(NOT_A_TX);
    }
}
