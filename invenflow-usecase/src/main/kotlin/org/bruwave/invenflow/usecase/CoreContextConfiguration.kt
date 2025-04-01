package org.bruwave.invenflow.usecase

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

// @SpringBootConfiguration
// @EnableMethodSecurity(securedEnabled = true)
// @PropertySources(
//    PropertySource("classpath:application.yaml", encoding = "utf-8"),
//    PropertySource("file:application.yaml", ignoreResourceNotFound = true, encoding = "utf-8")
// )
@Configuration
@EnableAutoConfiguration
@ComponentScan(
    basePackages = [
        "org.bruwave.invenflow.usecase",
    ],
)
@EnableCaching
class CoreContextConfiguration
