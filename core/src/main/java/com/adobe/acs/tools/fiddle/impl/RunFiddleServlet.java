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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.apache.sling.api.servlets.HttpConstants.METHOD_POST;

@Component(service = Servlet.class, immediate = true, property = {
        "sling.servlet.resourceTypes=" + "acs-tools/components/aemfiddle",
        "sling.servlet.methods=" + METHOD_POST,
        "sling.servlet.selectors=" + "run",
        "sling.servlet.extensions=" + "html",
})
public class RunFiddleServlet extends SlingAllMethodsServlet {
    private static final Logger log = LoggerFactory.getLogger(RunFiddleServlet.class);

    @Reference
    private FiddleRefresher fiddleRefresher;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        Resource resource = getResource(request);

        String data = request.getParameter("scriptdata");
        String ext = request.getParameter("scriptext");

        InMemoryScript script = InMemoryScript.set(ext, data);
        try {
            // doing this as a synchronous event, so we ensure that
            // the JSP has been invalidated
            fiddleRefresher.refresh(script.getPath());

            RequestDispatcherOptions options = new RequestDispatcherOptions();
            options.setForceResourceType(Constants.PSEDUO_COMPONENT_PATH);
            options.setReplaceSelectors("");

            RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
            dispatcher.forward(new SlingHttpServletRequestWrapper(request) {
                @Override
                public String getMethod() {
                    return "GET";
                }
            }, response);
        } finally {
            InMemoryScript.clear();
        }
    }

    private Resource getResource(SlingHttpServletRequest request) {
        String path = request.getParameter("resource");

        if (path == null || "".equals(path)) {
            return request.getResource();
        } else {
            return request.getResourceResolver().resolve(path);
        }
    }

}
