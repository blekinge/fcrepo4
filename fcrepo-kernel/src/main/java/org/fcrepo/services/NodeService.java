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

import static com.google.common.collect.ImmutableSet.builder;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;

import org.fcrepo.FedoraResource;
import org.fcrepo.utils.FedoraJcrTypes;
import org.fcrepo.utils.FedoraTypesUtils;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;

/**
 * Service for managing access to Fedora 'nodes' (either datastreams or objects, we don't care.)
 * @author Chris Beer
 * @date May 9, 2013
 */
@Component
public class NodeService extends RepositoryService implements FedoraJcrTypes {

    private static final Logger logger = getLogger(NodeService.class);

    /**
     * Find or create a new Fedora resource at the given path
     * @param session a JCR session
     * @param path a JCR path
     * @return
     * @throws RepositoryException
     */
    public FedoraResource findOrCreateObject(final Session session,
                                             final String path)
        throws RepositoryException {
        return new FedoraResource(findOrCreateNode(session, path));
    }

    /**
     * Retrieve an existing Fedora resource at the given path
     * @param session a JCR session
     * @param path a JCR path
     * @return
     * @throws RepositoryException
     */
    public FedoraResource getObject(final Session session,
                                    final String path)
        throws RepositoryException {
        return new FedoraResource(session.getNode(path));
    }

    /**
     * Get an existing Fedora resource at the given path with the given version
     * label
     * @param session a JCR session
     * @param path a JCR path
     * @param versionId a JCR version label
     * @return
     * @throws RepositoryException
     */
    public FedoraResource getObject(Session session,
                                    String path,
                                    String versionId)
        throws RepositoryException {
        final VersionHistory versionHistory =
            FedoraTypesUtils.getVersionHistory(session, path);

        if (versionHistory == null) {
            return null;
        }

        if (!versionHistory.hasVersionLabel(versionId)) {
            return null;
        }

        final Version version = versionHistory.getVersionByLabel(versionId);
        return new FedoraResource(version.getFrozenNode());
    }

    /**
     * @return A Set of object names (identifiers)
     * @throws RepositoryException
     */
    public Set<String> getObjectNames(final Session session, String path)
        throws RepositoryException {
        return getObjectNames(session, path, null);
    }

    /**
     * Get the list of child nodes at the given path filtered by the given mixin
     * @param session
     * @param path
     * @param mixin
     * @return
     * @throws RepositoryException
     */
    public Set<String> getObjectNames(final Session session,
                                      String path,
                                      String mixin) throws RepositoryException {

        final Node objects = session.getNode(path);
        final ImmutableSet.Builder<String> b = builder();
        final NodeIterator i = objects.getNodes();

        while (i.hasNext()) {
            Node n = i.nextNode();
            logger.info("child of type {} is named {} at {}",
                        n.getPrimaryNodeType(), n.getName(), n.getPath());

            if (mixin == null || n.isNodeType(mixin)) {
                b.add(n.getName());
            }
        }

        return b.build();
    }

    /**
     * Delete an existing object from the repository at the given path
     * @param session
     * @param path
     * @throws RepositoryException
     */
    public void deleteObject(final Session session, final String path)
        throws RepositoryException {
        final Node obj = session.getNode(path);
        obj.remove();
    }

}
