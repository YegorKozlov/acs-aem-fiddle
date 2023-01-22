package com.adobe.acs.tools.fiddle.impl;

import com.fleetcortechnologiesoperatingcompanyllcprogram3.core.servlets.SimpleServlet;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlethelpers.MockRequestDispatcherFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RunFiddleServletTest {

    @Mock
    private RequestDispatcher requestDispatcher;

    @Test
    void doGet(AemContext context) throws ServletException, IOException {
        context.registerInjectActivateService(new FiddleResourceProviderImpl());
        RunFiddleServlet fixture = context.registerInjectActivateService(new RunFiddleServlet());;


        MockSlingHttpServletRequest request = context.request();

        request.setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
                return requestDispatcher;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
                return requestDispatcher;
            }
        });
        MockSlingHttpServletResponse response = context.response();

        fixture.doPost(request, response);

    }

}
