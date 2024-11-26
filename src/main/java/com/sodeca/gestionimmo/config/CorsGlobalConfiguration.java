package com.sodeca.gestionimmo.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsGlobalConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Cela s'applique à toutes les routes de l'application
                .allowedOrigins("http://localhost:4200") // Remplacez par les origines que vous voulez autoriser
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Méthodes HTTP autorisées
                .allowedHeaders("*") // Tous les headers sont autorisés
                .allowCredentials(true) // Cookies et authentification
                .maxAge(3600); // Temps maximal pendant lequel la réponse à la requête préalable peut être mise en cache
    }
}