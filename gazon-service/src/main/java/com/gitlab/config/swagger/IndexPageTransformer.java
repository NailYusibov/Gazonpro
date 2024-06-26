package com.gitlab.config.swagger;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

public class IndexPageTransformer implements ResourceTransformer {
    @Override
    public Resource transform(HttpServletRequest request, Resource resource,
                              ResourceTransformerChain transformerChain) throws IOException {
        if (resource.getURL().toString().endsWith("/index.html")) {
            String html = getHtmlContent(resource);
            html = overwritePetStore(html);
            return new TransformedResource(resource, html.getBytes());
        } else {
            return resource;
        }
    }

    private String getHtmlContent(Resource resource) {
        try {
            InputStream inputStream = resource.getInputStream();
            java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            String content = s.next();
            inputStream.close();
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String overwritePetStore(String html) {
        return html.replace("https://petstore.swagger.io/v2/swagger.json",
                "/v3/api-docs");
    }
}
