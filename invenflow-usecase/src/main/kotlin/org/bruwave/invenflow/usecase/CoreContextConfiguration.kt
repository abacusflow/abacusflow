package org.bruwave.invenflow.usecase

import org.bruwave.invenflow.commons.YamlPropertySourceFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

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
@EnableJpaRepositories(
    basePackages = [
        "org.bruwave.invenflow.user",
    ],
)
@EntityScan(
    basePackages = [
        "org.bruwave.invenflow.user",
    ],
)
@PropertySource("classpath:application-core.yml", factory = YamlPropertySourceFactory::class)
class CoreContextConfiguration
