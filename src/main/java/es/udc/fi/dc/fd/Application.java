package es.udc.fi.dc.fd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**S
 * Aplicación principal.
 */
@EnableScheduling
@SpringBootApplication
public class Application {
    /**
     * Configuración global para permitir peticiones desde el frontend.
     */
    @Bean
    public org.springframework.web.servlet.config.annotation.WebMvcConfigurer corsConfigurer() {
        return new org.springframework.web.servlet.config.annotation.WebMvcConfigurer() {
            @Override
            public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

	/**
	 * El método main.
	 *
	 * @param args los argumentos
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	/**
	 * Encriptador de contraseñas.
	 *
	 * @return el encriptador de contraseñas
	 */
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    
    /**
     * Fuente del mensaje.
     *
     * @return la fuente del mensaje
     */
    @Bean
    public MessageSource messageSource() {
    	
        ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
        
        bean.setBasename("classpath:messages");
        bean.setDefaultEncoding("UTF-8");
        
        return bean;
    }
    
    /**
     * Validador.
     *
     * @return el validador
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
    	
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        
        bean.setValidationMessageSource(messageSource());
        
        return bean;
    }

}
