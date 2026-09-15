package com.energyassessment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the existing static frontend (served from any typical local dev
 * server on localhost/127.0.0.1) to call this API.
 *
 * If you open index.html directly as a file:// URL, the browser sends
 * "Origin: null" and most browsers will not attach a usable Origin header
 * for fetch() at all — CORS then behaves inconsistently. Serve the frontend
 * with a simple local server instead, e.g. from the frontend/ folder:
 *   python3 -m http.server 5500
 * and open http://localhost:5500/index.html.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
registry.addMapping("/api/**")
        .allowedOriginPatterns(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://urja-check.vercel.app",
                "https://*.vercel.app"
        )
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(false)
        .maxAge(3600);