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

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.spi.resource.provider.ResolveContext;
import org.apache.sling.spi.resource.provider.ResourceContext;
import org.apache.sling.spi.resource.provider.ResourceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.adobe.acs.tools.fiddle.impl.Constants.PSEDUO_COMPONENT_PATH;

@Component(
        immediate = true,
        service = FiddleRefresher.class
)
public class FiddleResourceProviderImpl extends ResourceProvider<Object> implements FiddleRefresher {
    private static final Logger log = LoggerFactory.getLogger(FiddleResourceProviderImpl.class);

    private ServiceRegistration<ResourceProvider> resourceProviderRegistration;

    private Resource script;

    @Activate
    protected void activate(BundleContext context) {
        Dictionary<String, Object> props = new Hashtable<>();

        props.put(ResourceProvider.PROPERTY_NAME, "acs-aem-tools.aem-fiddle");
        props.put(ResourceProvider.PROPERTY_ROOT, PSEDUO_COMPONENT_PATH);
        props.put(ResourceProvider.PROPERTY_REFRESHABLE, true);
        resourceProviderRegistration = context.registerService(ResourceProvider.class, this, props);
    }

    @Deactivate
    protected void deactivate() {
        resourceProviderRegistration.unregister();
    }

    @Override
    public Resource getResource(ResolveContext ctx, String path, ResourceContext resourceContext, Resource parent) {
        ResourceResolver resourceResolver = ctx.getResourceResolver();
        InMemoryScript script = InMemoryScript.get();
        if (script != null && path.equals(script.getPath())) {
            return script.toResource(resourceResolver);
        }

        return null;
    }

    @Override
    public Iterator<Resource> listChildren(ResolveContext ctx, Resource parent) {
        if (parent.getPath().equals(PSEDUO_COMPONENT_PATH)) {
            InMemoryScript script = InMemoryScript.get();
            if (script != null) {
                Resource scriptResource = script.toResource(parent.getResourceResolver());
                return Collections.singleton(scriptResource).iterator();
            }
        }
        return null;
    }

    @Override
    public void refresh(String path) {
        if (getProviderContext() != null) {
            List<ResourceChange> resourceChangeList = new ArrayList<>();
            ResourceChange resourceChange = new ResourceChange(
                    ResourceChange.ChangeType.CHANGED,
                    path,
                    false
            );

            resourceChangeList.add(resourceChange);
            getProviderContext().getObservationReporter().reportChanges(resourceChangeList, false);
        } else {
            log.warn("Unable to obtain a Observation Changer for AEM Fiddle script resource provider");
        }
    }


}