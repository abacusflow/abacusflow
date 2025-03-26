package org.bruwave.invenflow.usecase

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources


//@SpringBootConfiguration
@Configuration
@EnableAutoConfiguration
@ComponentScan(
    basePackages = [
        "org.bruwave.invenflow.usecase",
    ]
)
@EnableCaching
//@EnableMethodSecurity(securedEnabled = true)
//@PropertySources(
//    PropertySource("classpath:application.yaml", encoding = "utf-8"),
//    PropertySource("file:application.yaml", ignoreResourceNotFound = true, encoding = "utf-8")
//)
class CoreContextConfiguration {
}