package com.scc.pdf.core.servlets;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by John Dorrance on 11/9/2016.
 */
@SlingServlet(
        name = "Generic PDF Rendition Servlet",
        selectors = "docrender",
        extensions = "pdf",
        resourceTypes = {"foundation/components/page","wcm/foundation/components/page","pdf-generator/components/structure/page"},
        methods = "GET",
        metatype = false)
public class GenericPDFRendererServlet extends SlingSafeMethodsServlet {

    private Logger log = LoggerFactory.getLogger(GenericPDFRendererServlet.class);

    @Override
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        log.error(request.getResource().getPath());
        String path = request.getResource().getPath();
        response.setContentType("application/pdf");
        byte[] pdf = getServerResponse("http://pdfgenerator:8080/convert?auth=arachnys-weaver&amp;url=http://author:4502" + path + ".docrender.html%3Fwcmmode=disabled");
        response.getOutputStream().write(pdf);

    }
    private byte[] getServerResponse(String url) throws IOException {
        HttpClient client = new HttpClient();

        HttpMethod method = null;
        String getURL = url;
        method = new GetMethod(getURL );

        byte[] response = null;
        try{
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                log.error("Method failed: " + method.getStatusLine());

                throw new IOException("Method failed: " + method.getResponseBodyAsString());
            }else{
                response = method.getResponseBody();
            }
        }finally {
            method.releaseConnection();
        }
        return  response;
    }
}
