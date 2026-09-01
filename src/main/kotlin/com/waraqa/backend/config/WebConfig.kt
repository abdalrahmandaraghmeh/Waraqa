package com.waraqa.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val uploadDir = Paths.get("uploads").toAbsolutePath().toUri().toString()
        val location = if (uploadDir.endsWith("/")) uploadDir else "$uploadDir/"

        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(location)
    }
}
