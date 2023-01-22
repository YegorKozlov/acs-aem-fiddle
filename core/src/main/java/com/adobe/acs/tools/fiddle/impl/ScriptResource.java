package com.adobe.acs.tools.fiddle.impl;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ScriptResource extends SyntheticResource {
    private final String data;

    public ScriptResource(ResourceResolver resourceResolver, String path, String data) {
        super(resourceResolver,  path, JcrConstants.NT_FILE);
        this.data = data;
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
    public Resource getParent() {
        String parentPath = ResourceUtil.getParent(getPath());
        return new SyntheticResource(getResourceResolver(), parentPath, JcrConstants.NT_FOLDER);
    }

}
