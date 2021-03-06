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
package org.fcrepo.syndication;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.fcrepo.FedoraResource;
import org.fcrepo.rdf.GraphSubjects;
import org.fcrepo.rdf.impl.DefaultGraphSubjects;
import org.fcrepo.test.util.TestHelpers;
import org.fcrepo.utils.FedoraJcrTypes;
import org.junit.Before;
import org.junit.Test;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.ws.rs.core.UriInfo;

import static org.fcrepo.RdfLexicon.HAS_FEED;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RssResourcesTest {
    private RssResources testObj;
    private Node mockNode;
    private FedoraResource mockResource;
    private UriInfo uriInfo;
    private GraphSubjects mockSubjects;

    @Before
    public void setUp() {
        testObj = new RssResources();
        mockNode = mock(Node.class);
        mockResource = new FedoraResource(mockNode);

        uriInfo = TestHelpers.getUriInfoImpl();
        mockSubjects = new DefaultGraphSubjects();
    }

    @Test
    public void shouldDecorateModeRootNodesWithRepositoryWideLinks() throws RepositoryException {

        final NodeType mockNodeType = mock(NodeType.class);
        when(mockNodeType.isNodeType(FedoraJcrTypes.ROOT)).thenReturn(true);
        when(mockNode.getPrimaryNodeType()).thenReturn(mockNodeType);
        when(mockNode.getPath()).thenReturn("/");

        Resource graphSubject = mockSubjects.getGraphSubject(mockNode);

        final Model model = testObj.createModelForResource(mockResource, uriInfo, mockSubjects);

        assertTrue(model.contains(graphSubject, HAS_FEED));
    }
}
