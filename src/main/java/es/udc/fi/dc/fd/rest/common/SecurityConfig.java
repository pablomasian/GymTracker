package es.udc.fi.dc.fd.rest.common;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@org.springframework.context.annotation.Profile("!test")
public class SecurityConfig {
        // Configuración de seguridad: endpoints públicos, JWT y CORS

        @Autowired
        private JwtFilter jwtFilter;

        @Bean
        protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .cors(Customizer.withDefaults())
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                antMatcher("/h2-console/**"),
                                                                antMatcher("/actuator/health"),
                                                                antMatcher("/"), // /
                                                                antMatcher("/index.html"), // index.html
                                                                antMatcher("/static/**"),
                                                                antMatcher("/assets/**"),
                                                                antMatcher("/manifest.json"),
                                                                antMatcher("/favicon.ico"),
                                                                antMatcher("/*.png"),
                                                                antMatcher("/*.ico"),
                                                                antMatcher("/api/users/signUp"),
                                                                antMatcher("/api/users/login"),
                                                                antMatcher("/api/users/loginFromServiceToken"),
                                                                antMatcher("/api/users/*/avatar"),
                                                                antMatcher("/uploads/**"),
                                                                antMatcher("/api/routines/display_all"),
                                                                antMatcher(HttpMethod.GET, "/api/routines"),
                                                                antMatcher(HttpMethod.GET, "/api/routines/*"),
                                                                antMatcher(HttpMethod.GET, "/api/routines/*/detail"),
                                                                antMatcher(HttpMethod.GET, "/api/routines/*/exercises"),
                                                                antMatcher(HttpMethod.GET, "/api/exercises"),
                                                                antMatcher("/api/hello"),
                                                                antMatcher(HttpMethod.GET,
                                                                                "/api/users/*/public-profile"),
                                                                antMatcher(HttpMethod.GET,
                                                                                "/api/users/*/public-profile/**"),
                                                                antMatcher(HttpMethod.GET,
                                                                                "/api/users/*/following-list"),
                                                                antMatcher(HttpMethod.GET,
                                                                                "/api/users/*/followers-list"))
                                                .permitAll()

                                                .requestMatchers(
                                                                antMatcher(HttpMethod.POST, "/api/routines"),
                                                                antMatcher(HttpMethod.GET, "/api/users/badges"),
                                                                antMatcher("/api/routines/my-routines"),
                                                                antMatcher(HttpMethod.POST, "/api/routines/*/publish"),
                                                                antMatcher(HttpMethod.POST, "/api/routines/*/hide"),
                                                                antMatcher(HttpMethod.PUT, "/api/routines/*"),
                                                                antMatcher(HttpMethod.DELETE, "/api/routines/*"),
                                                                antMatcher(HttpMethod.POST, "/api/exercises"),
                                                                antMatcher(HttpMethod.GET, "/api/exercises/pending"),
                                                                antMatcher("/api/saved-routines/**"),
                                                                antMatcher("/api/workouts/log"),
                                                                antMatcher("/api/workouts/user-sessions"),
                                                                antMatcher("/api/workouts/**"),
                                                                antMatcher("/api/workouts/start/**"),
                                                                antMatcher("/api/workouts/finish/**"),
                                                                antMatcher("/api/feed"),
                                                                antMatcher(HttpMethod.GET, "/api/users/search"),
                                                                antMatcher(HttpMethod.DELETE,
                                                                                "/api/users/coach-profile/*"))
                                                .authenticated()
                                                .anyRequest().authenticated())

                                .headers(headers -> headers.frameOptions().sameOrigin());

                http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                config.addAllowedOrigin("http://localhost:3000");
                config.addAllowedOrigin("https://deploy.fic.udc.es");

                config.addAllowedHeader("*");
                config.addAllowedMethod("GET");
                config.addAllowedMethod("POST");
                config.addAllowedMethod("PUT");
                config.addAllowedMethod("DELETE");
                config.addAllowedMethod("OPTIONS");
                config.setAllowCredentials(true);
                config.addExposedHeader("Authorization");

                source.registerCorsConfiguration("/**", config);
                return source;
        }
}