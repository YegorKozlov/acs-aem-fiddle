/*
 * #%L
 * ACS AEM Tools Bundle
 * %%
 * Copyright (C) 2013 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.adobe.acs.tools.fiddle.impl;

import com.adobe.acs.tools.fiddle.FiddleHelper;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.InputStream;

import static com.day.cq.security.util.RequestConstants.ENCODING_UTF_8;

@Component
public class FiddleHelperImpl implements FiddleHelper {
    private static final Logger log = LoggerFactory.getLogger(FiddleHelperImpl.class);

    @Override
    public String getCodeTemplate(final Resource resource) {
        try {
            return getNTFileAsString(resource);
        } catch (RepositoryException ex) {
            log.error("Unable to get the AEM Fiddle code template from resource [ {} ] due to: {}",
                    resource.getPath(), ex);
            return "";
        } catch (IOException ex) {
            log.error("Unable to get the AEM Fiddle code template from resource [ {} ] due to: {}",
                    resource.getPath(), ex);
            return "";
        }
    }

    public static InputStream getNTFileAsInputStream(Resource resource) throws RepositoryException {
        Node node = resource.adaptTo(Node.class);
        Node jcrContent = node.getNode("jcr:content");
        return jcrContent.getProperty("jcr:data").getBinary().getStream();
    }

    public static String getNTFileAsString(Resource resource) throws RepositoryException, IOException {
        InputStream inputStream = getNTFileAsInputStream(resource);
        return IOUtils.toString(inputStream, ENCODING_UTF_8);
    }
}
