package com.gitlab.config.swagger;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class IndexPageTransformer implements ResourceTransformer {
    @Override
    public Resource transform(HttpServletRequest request, Resource resource,
                              ResourceTransformerChain transformerChain) throws IOException {
        if (resource.getURL().toString().endsWith("/index.html")) {
            String html = getHtmlContent(resource);
            html = overwritePetStore(html);
            return new TransformedResource(resource, html.getBytes(StandardCharsets.UTF_8));
        } else {
            return resource;
        }
    }

    private String getHtmlContent(Resource resource) {
        try {
            InputStream inputStream = resource.getInputStream();
            java.util.Scanner scanner = new java.util.Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A");
            String content = scanner.next();
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
