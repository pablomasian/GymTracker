package es.udc.fi.dc.fd.rest.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test") 
public class SecurityConfigTest {

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Test
    public void securityFilterChainLoads() {
        assertNotNull(springSecurityFilterChain);
    }

    @Test
    public void corsConfigurationContainsLocalhostOrigin() {
        HttpServletRequest dummy = new org.springframework.mock.web.MockHttpServletRequest();
        CorsConfiguration cfg = corsConfigurationSource.getCorsConfiguration(dummy);
        assertNotNull(cfg);
        org.junit.jupiter.api.Assertions.assertTrue(cfg.getAllowedOrigins().contains("http://localhost:3000"));
    }
}