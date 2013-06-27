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

package org.fcrepo.api.rdf;

import javax.jcr.RepositoryException;
import javax.ws.rs.core.UriInfo;

import org.fcrepo.FedoraResource;
import org.fcrepo.rdf.GraphSubjects;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Helper to generate an RDF model for a FedoraResource that (likely) creates
 * relations from our resource to other HTTP components
 */
public interface UriAwareResourceModelFactory {

    Model createModelForResource(final FedoraResource resource,
            final UriInfo uriInfo, GraphSubjects graphSubjects)
        throws RepositoryException;
}
