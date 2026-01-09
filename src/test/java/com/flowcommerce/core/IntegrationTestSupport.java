package com.flowcommerce.core;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public abstract class IntegrationTestSupport {
}
