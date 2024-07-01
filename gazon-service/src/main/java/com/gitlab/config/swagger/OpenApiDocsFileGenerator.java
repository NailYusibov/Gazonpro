package com.gitlab.config.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;

@Configuration
public class OpenApiDocsFileGenerator {

    @EventListener
    public void generateOpenApiDocs(ContextRefreshedEvent event) {
        try (var readableByteChannel = Channels.newChannel(
                new URL("http://localhost:8080/v3/api-docs").openStream());
             var fileOutputStream = new FileOutputStream("openApi.json")) {
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}