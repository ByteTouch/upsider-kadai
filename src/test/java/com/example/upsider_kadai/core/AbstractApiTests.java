package com.example.upsider_kadai.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class AbstractApiTests extends AbstractSpringBootTests {
    @Autowired
    protected MockMvc mockMvc;
}
