package dev.sharkbox.api;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import(SharkboxApiTestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class SharkboxApiTestBase {
    
}
