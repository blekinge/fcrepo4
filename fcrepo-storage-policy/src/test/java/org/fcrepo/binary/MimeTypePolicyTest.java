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
package org.fcrepo.binary;

import org.junit.Test;
import org.modeshape.jcr.api.JcrConstants;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.modeshape.jcr.api.JcrConstants.JCR_MIME_TYPE;

/**
 * @todo Add Documentation.
 * @author cbeer
 * @date Apr 25, 2013
 */
public class MimeTypePolicyTest {

    /**
     * @todo Add Documentation.
     */
    @Test
    public void shouldEvaluatePolicyAndReturnHint() throws Exception {
        String hint = "store-id";
        Policy policy = new MimeTypePolicy("image/x-dummy", hint);

        Session mockSession = mock(Session.class);
        Node mockRootNode = mock(Node.class);
        Node mockDsNode = mock(Node.class);

        when(mockDsNode.getSession()).thenReturn(mockSession);
        Property mockProperty = mock(Property.class);
        when(mockProperty.getString()).thenReturn("image/x-dummy");
        Node mockContentNode = mock(Node.class);
        when(mockDsNode.getNode(JcrConstants.JCR_CONTENT)).thenReturn(
                mockContentNode);
        when(mockContentNode.getProperty(JCR_MIME_TYPE)).thenReturn(
                mockProperty);

        String receivedHint = policy.evaluatePolicy(mockDsNode);

        assertThat(receivedHint, is(hint));
    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void shouldEvaluatePolicyAndReturnNoHint() throws Exception {
        String hint = "store-id";
        Policy policy = new MimeTypePolicy("image/x-dummy", hint);

        Session mockSession = mock(Session.class);
        Node mockDsNode = mock(Node.class);

        when(mockDsNode.getSession()).thenReturn(mockSession);
        Property mockProperty = mock(Property.class);
        when(mockProperty.getString()).thenReturn("application/x-other");
        Node mockContentNode = mock(Node.class);
        when(mockDsNode.getNode(JcrConstants.JCR_CONTENT)).thenReturn(
                mockContentNode);
        when(mockContentNode.getProperty(JCR_MIME_TYPE)).thenReturn(
                mockProperty);

        String receivedHint = policy.evaluatePolicy(mockDsNode);

        assertNull(receivedHint);
    }

    /**
     * @todo Add Documentation.
     */
    @Test
    public void shouldEvaluatePolicyAndReturnNoHintOnException()
        throws Exception {
        String hint = "store-id";
        Policy policy = new MimeTypePolicy("image/x-dummy", hint);

        Session mockSession = mock(Session.class);
        Node mockDsNode = mock(Node.class);

        when(mockDsNode.getNode(JcrConstants.JCR_CONTENT)).thenThrow(
                new RepositoryException());

        String receivedHint = policy.evaluatePolicy(mockDsNode);

        assertNull(receivedHint);
    }
}
