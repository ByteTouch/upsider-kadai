package com.example.upsider_kadai.core;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({ TestConfig.class })
public abstract class AbstractSpringBootTests extends AbstractTestcontainers {
}
