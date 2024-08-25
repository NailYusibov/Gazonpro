package com.gitlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.config.PostgresSqlContainer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles({"test", "security"})
@TestPropertySource("/application-test.yml")
@SpringBootTest
@ContextConfiguration(initializers = {
        PostgresSqlContainer.Initializer.class
})
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    protected static final String URL = "http://localhost:8089";

    @BeforeAll
    static void init() {
        PostgresSqlContainer.container.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}