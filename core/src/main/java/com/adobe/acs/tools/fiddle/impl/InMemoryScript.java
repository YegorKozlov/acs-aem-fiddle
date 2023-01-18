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

import com.day.cq.commons.jcr.JcrConstants;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class InMemoryScript {

    private final static ThreadLocal<InMemoryScript> holder = new ThreadLocal<>();

    private final String data;
    private final String path;

    private InMemoryScript(String ext, String data) {
        this.data = data;
        this.path = Constants.SCRIPT_PATH + "." + ext;
    }

    public static InMemoryScript set(String ext, String data) {
        InMemoryScript value = new InMemoryScript(ext, data);
        holder.set(value);
        return value;
    }

    public static InMemoryScript get() {
        return holder.get();
    }

    public static void clear() {
        holder.remove();
    }

    public Resource toResource(ResourceResolver resourceResolver) {
        return new ScriptResource(resourceResolver);
    }

    public String getPath() {
        return path;
    }

    private class ScriptResource extends SyntheticResource {

        public ScriptResource(ResourceResolver resourceResolver) {
            super(resourceResolver, path, JcrConstants.NT_FILE);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
            if (type == InputStream.class) {
                return (AdapterType) new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
            } else if (type == ValueMap.class) {
                return (AdapterType) ValueMapDecorator.EMPTY;
            } else {
                return super.adaptTo(type);
            }
        }

        @Override
        public Resource getChild(String relPath) {
            if (JcrConstants.JCR_CONTENT.equals(relPath)) {
                return new ScriptPropertiesResource(getResourceResolver());
            } else {
                return null;
            }
        }
        
        @Override
        public Resource getParent() {
            return new ScriptParentResource(this);
        }
    }

    private class ScriptPropertiesResource extends SyntheticResource {

        private final ValueMap properties;

        public ScriptPropertiesResource(ResourceResolver resourceResolver) {
            super(resourceResolver, path + "/" + JcrConstants.JCR_CONTENT, JcrConstants.NT_UNSTRUCTURED);
            Map<String, Object> map = Collections.singletonMap(JcrConstants.JCR_ENCODING, StandardCharsets.UTF_8);
            properties = new ValueMapDecorator(map);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
            if (type == ValueMap.class) {
                return (AdapterType) properties;
            }
            return super.adaptTo(type);
        }

    }

    public class ScriptParentResource extends SyntheticResource {

        private final ScriptResource scriptResource;

        public ScriptParentResource(ScriptResource scriptResource) {
            super(scriptResource.getResourceResolver(), Text.getAbsoluteParent(scriptResource.getPath(), 1), null);
            this.scriptResource = scriptResource;
        }
        
        @Override
        public Resource getChild(String relPath) {
            if (relPath.equals(scriptResource.getName())) {
                return scriptResource;
            } else {
                return null;
            }
        }

    }


}
