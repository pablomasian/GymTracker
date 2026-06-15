package es.udc.fi.dc.fd.rest.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

class SecurityConfigUnitTest {

    @Test
    void corsConfigurationSource_containsExpectedSettings() {
        SecurityConfig cfg = new SecurityConfig();
        CorsConfigurationSource source = cfg.corsConfigurationSource();

        assertThat(source).isInstanceOf(UrlBasedCorsConfigurationSource.class);

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/any/path");
        CorsConfiguration conf = ((UrlBasedCorsConfigurationSource) source).getCorsConfiguration(req);

        assertThat(conf).isNotNull();
        assertThat(conf.getAllowedOrigins()).contains("http://localhost:3000");
        assertThat(conf.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(conf.getAllowedHeaders()).contains("*");
        assertThat(conf.getExposedHeaders()).contains("Authorization");
    }

}
