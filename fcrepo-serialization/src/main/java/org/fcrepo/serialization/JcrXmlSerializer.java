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

package org.fcrepo.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.fcrepo.FedoraObject;
import org.springframework.stereotype.Component;

@Component
public class JcrXmlSerializer extends BaseFedoraObjectSerializer {

    @Override
    public String getKey() {
        return "jcr/xml";
    }

    @Override
    public String getMediaType() {
        return "application/xml";
    }

    @Override
    public void serialize(final FedoraObject obj, final OutputStream out)
        throws RepositoryException, IOException {
        final Node node = obj.getNode();
        node.getSession().exportSystemView(node.getPath(), out, false, false);
    }

    @Override
    public void deserialize(final Session session, final String path,
            final InputStream stream) throws RepositoryException, IOException {

        session.importXML(path, stream,
                ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW);

    }

}
