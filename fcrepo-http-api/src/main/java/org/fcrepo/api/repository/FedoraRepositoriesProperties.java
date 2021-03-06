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

package org.fcrepo.api.repository;

import static javax.ws.rs.core.Response.status;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.WebContent;
import org.fcrepo.AbstractResource;
import org.fcrepo.FedoraResource;
import org.fcrepo.api.FedoraNodes;
import org.fcrepo.api.rdf.HttpGraphSubjects;
import org.fcrepo.session.InjectedSession;
import org.modeshape.common.collection.Problems;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Timed;

@Component
@Scope("prototype")
@Path("/fcr:properties")
public class FedoraRepositoriesProperties extends AbstractResource {

    @InjectedSession
    protected Session session;

    private static final Logger logger =
            getLogger(FedoraRepositoriesProperties.class);

    /**
     * Update an object using SPARQL-UPDATE
     * 
     * @return 201
     * @throws javax.jcr.RepositoryException
     * @throws java.io.IOException
     */
    @POST
    @Consumes({WebContent.contentTypeSPARQLUpdate})
    @Timed
    public Response updateSparql(final InputStream requestBodyStream)
        throws RepositoryException, IOException {

        try {
            if (requestBodyStream != null) {

                final FedoraResource result =
                        nodeService.getObject(session, "/");

                result.updatePropertiesDataset(new HttpGraphSubjects(
                        FedoraNodes.class, uriInfo, session), IOUtils
                        .toString(requestBodyStream));
                final Problems problems = result.getDatasetProblems();
                if (problems != null && problems.hasProblems()) {
                    logger.info(
                            "Found these problems updating the properties for {}: {}",
                            "/", problems.toString());
                    return status(Response.Status.FORBIDDEN).entity(
                            problems.toString()).build();
                }

                session.save();

                return status(SC_NO_CONTENT).build();
            } else {
                return status(SC_BAD_REQUEST).entity(
                        "SPARQL-UPDATE requests must have content ").build();
            }
        } finally {
            session.logout();
        }
    }

    public void setSession(final Session session) {
        this.session = session;
    }
}
