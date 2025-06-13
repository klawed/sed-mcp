package dev.klawed.sedmcp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.security.user.name=test",
    "spring.security.user.password=test"
})
class ApplicationTests {

    @Test
    void contextLoads() {
        // This test will pass if the Spring context loads successfully
        // It validates that all beans can be created and autowired properly
    }
}
