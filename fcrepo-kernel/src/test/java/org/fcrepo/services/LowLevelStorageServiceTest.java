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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.fcrepo.services.functions.GetBinaryKey;
import org.fcrepo.services.functions.GetCacheStore;
import org.fcrepo.utils.LowLevelCacheEntry;
import org.fcrepo.utils.impl.CacheStoreEntry;
import org.fcrepo.utils.impl.LocalBinaryStoreEntry;
import org.infinispan.Cache;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.loaders.CacheStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.jcr.GetBinaryStore;
import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.value.BinaryKey;
import org.modeshape.jcr.value.binary.BinaryStore;
import org.modeshape.jcr.value.binary.CompositeBinaryStore;
import org.modeshape.jcr.value.binary.infinispan.InfinispanBinaryStore;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * @todo Add Documentation.
 * @autho Chris Beerr
 * @date Mar 11, 2013
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"org.slf4j.*", "javax.xml.parsers.*", "org.apache.xerces.*"})
@PrepareForTest({ServiceHelpers.class})
public class LowLevelStorageServiceTest {

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testTransformBinaryBlobs() throws RepositoryException {
        final GetBinaryStore mockStoreFunc = mock(GetBinaryStore.class);
        final GetBinaryKey mockKeyFunc = mock(GetBinaryKey.class);
        final Node mockNode = mock(Node.class);
        final Repository mockRepo = mock(Repository.class);
        final BinaryKey mockKey = mock(BinaryKey.class);
        final BinaryStore mockStore = mock(BinaryStore.class);

        final Property mockProperty = mock(Property.class);
        when(mockNode.getProperty(JcrConstants.JCR_DATA)).thenReturn(mockProperty);
        when(mockStore.toString()).thenReturn("foo");
        when(mockKeyFunc.apply(mockProperty)).thenReturn(mockKey);
        when(mockStoreFunc.apply(mockRepo)).thenReturn(mockStore);
        final LowLevelStorageService testObj = new LowLevelStorageService();
        testObj.setGetBinaryStore(mockStoreFunc);
        testObj.setGetBinaryKey(mockKeyFunc);
        testObj.setRepository(mockRepo);
        @SuppressWarnings("unchecked")
        final Function<LowLevelCacheEntry, String> testFunc =
                mock(Function.class);
        when(testFunc.apply(any(LowLevelCacheEntry.class))).thenReturn("bar");
        final Collection<String> actual =
                testObj.transformLowLevelCacheEntries(mockNode, testFunc);
        assertEquals("bar", actual.iterator().next());
        verify(testFunc).apply(any(LowLevelCacheEntry.class));
    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void testGetBinaryBlobs() throws RepositoryException {
        final GetBinaryStore mockStoreFunc = mock(GetBinaryStore.class);
        final GetBinaryKey mockKeyFunc = mock(GetBinaryKey.class);
        final Node mockNode = mock(Node.class);
        final Property mockProperty = mock(Property.class);
        when(mockNode.getProperty(JcrConstants.JCR_DATA)).thenReturn(mockProperty);
        final Repository mockRepo = mock(Repository.class);
        final BinaryKey mockKey = mock(BinaryKey.class);
        final BinaryStore mockStore = mock(BinaryStore.class);
        when(mockStore.toString()).thenReturn("foo");
        when(mockKeyFunc.apply(mockProperty)).thenReturn(mockKey);
        when(mockStoreFunc.apply(mockRepo)).thenReturn(mockStore);
        final LowLevelStorageService testObj = new LowLevelStorageService();
        testObj.setGetBinaryStore(mockStoreFunc);
        testObj.setGetBinaryKey(mockKeyFunc);
        testObj.setRepository(mockRepo);
        final Set<LowLevelCacheEntry> actual =
                testObj.getLowLevelCacheEntries(mockNode);
        assertEquals("/foo", actual.iterator().next().getExternalIdentifier());
    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void shouldRetrieveLowLevelCacheEntryForDefaultBinaryStore()
            throws RepositoryException {
        final BinaryKey key = new BinaryKey("key-123");
        final GetBinaryStore mockStoreFunc = mock(GetBinaryStore.class);
        final Repository mockRepo = mock(Repository.class);
        final BinaryStore mockStore = mock(BinaryStore.class);
        when(mockStoreFunc.apply(mockRepo)).thenReturn(mockStore);

        final LowLevelStorageService testObj =
                spy(new LowLevelStorageService());
        testObj.setRepository(mockRepo);
        testObj.setGetBinaryStore(mockStoreFunc);
        testObj.getLowLevelCacheEntries(key);
        verify(testObj, times(1)).getLowLevelCacheEntriesFromStore(mockStore,
                key);
    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void shouldRetrieveLowLevelCacheStoresForBinaryKey()
            throws RepositoryException {

        final BinaryStore mockStore = mock(BinaryStore.class);

        final LowLevelStorageService testObj = new LowLevelStorageService();

        final Set<LowLevelCacheEntry> entries =
                testObj.getLowLevelCacheEntriesFromStore(mockStore,
                        new BinaryKey("key-123"));

        assertEquals(1, entries.size());

        assertTrue("does not contain our entry", entries
                .contains(new LocalBinaryStoreEntry(mockStore, new BinaryKey(
                        "key-123"))));
    }

    /**
     * @throws Exception 
     * @todo Add Documentation.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void shouldRetrieveLowLevelCacheStoresForCompositeStore()
            throws Exception {

        mockStatic(ServiceHelpers.class);
        
        final Cache<?, ?> ispnCache1 = mock(Cache.class);
        final Cache<?, ?> ispnCache2 = mock(Cache.class);
        final CacheStore ispnCacheStore1 = mock(CacheStore.class);
        final CacheStore ispnCacheStore2 = mock(CacheStore.class);
        final BinaryStore plainBinaryStore = mock(BinaryStore.class);
        final BinaryStore plainBinaryStore2 = mock(BinaryStore.class);

        final GetCacheStore mockCacheStoreFunc = mock(GetCacheStore.class);
        when(mockCacheStoreFunc.apply(ispnCache1)).thenReturn(ispnCacheStore1);
        when(mockCacheStoreFunc.apply(ispnCache2)).thenReturn(ispnCacheStore2);

        final CompositeBinaryStore mockStore = mock(CompositeBinaryStore.class);

        final HashMap<String, BinaryStore> map =
                new HashMap<String, BinaryStore>();
        final List<Cache<?, ?>> caches = new ArrayList<Cache<?, ?>>();
        caches.add(ispnCache1);
        caches.add(ispnCache2);

        map.put("default", plainBinaryStore);
        map.put("a", plainBinaryStore2);
        final InfinispanBinaryStore infinispanBinaryStore =
                mock(InfinispanBinaryStore.class);
        when(infinispanBinaryStore.getCaches()).thenReturn(caches);
        map.put("b", infinispanBinaryStore);
        when(mockStore.getNamedStoreIterator()).thenReturn(
                map.entrySet().iterator());

        final DistributedExecutorService mockCluster =
                mock(DistributedExecutorService.class);
        when(ServiceHelpers.getClusterExecutor(infinispanBinaryStore))
            .thenReturn(mockCluster);

        final BinaryKey key = new BinaryKey("key-123");
        
        LowLevelCacheEntry cacheEntry1 =
                new CacheStoreEntry(ispnCacheStore1,
                        "cache1",
                        key);
        ImmutableSet.Builder<LowLevelCacheEntry> builder = ImmutableSet.builder();
        Set<LowLevelCacheEntry> cacheResponse1 = builder.add(cacheEntry1).build();
        builder = ImmutableSet.builder();
        LowLevelCacheEntry cacheEntry2 =
                new CacheStoreEntry(ispnCacheStore2,
                        "cache2",
                        key);
        Set<LowLevelCacheEntry> cacheResponse2 = builder.add(cacheEntry2).build();
        Future<Collection<LowLevelCacheEntry>> future1 = mock(Future.class);
        Future<Collection<LowLevelCacheEntry>> future2 = mock(Future.class);
        when(future1.get(any(Long.class), eq(TimeUnit.MILLISECONDS)))
            .thenReturn(cacheResponse1);
        when(future2.get(any(Long.class), eq(TimeUnit.MILLISECONDS)))
        .thenReturn(cacheResponse2);

        List<Future<?>> mockClusterResults = new ArrayList<Future<?>>(2);
        mockClusterResults.add(future1);
        mockClusterResults.add(future2);

        when(mockCluster.submitEverywhere(any(org.fcrepo.services.functions.CacheLocalTransform.class)))
            .thenReturn(mockClusterResults);

        
        final LowLevelStorageService testObj = new LowLevelStorageService();

        when(plainBinaryStore.hasBinary(key)).thenReturn(true);
        when(plainBinaryStore2.hasBinary(key)).thenReturn(false);
        when(infinispanBinaryStore.hasBinary(key)).thenReturn(true);
        final Set<LowLevelCacheEntry> entries =
                testObj.getLowLevelCacheEntriesFromStore(mockStore, key);

        assertEquals(3, entries.size());

        assertTrue(entries.contains(new LocalBinaryStoreEntry(plainBinaryStore,
                key)));
        assertFalse(entries.contains(new LocalBinaryStoreEntry(plainBinaryStore2,
                key)));
        assertTrue(entries.contains(new CacheStoreEntry(
                ispnCacheStore1, "cache1", key)));
        assertTrue(entries.contains(new CacheStoreEntry(
                ispnCacheStore2, "cache2", key)));

    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void shouldReturnAnEmptySetForMissingBinaryStore()
            throws RepositoryException {

        final GetBinaryStore mockStoreFunc = mock(GetBinaryStore.class);
        final Repository mockRepo = mock(Repository.class);
        when(mockStoreFunc.apply(mockRepo)).thenReturn(null);

        final LowLevelStorageService testObj = new LowLevelStorageService();
        testObj.setGetBinaryStore(mockStoreFunc);
        final Set<LowLevelCacheEntry> entries =
                testObj.getLowLevelCacheEntries(new BinaryKey("key-123"));

        assertEquals(0, entries.size());
    }

}
