package org.bruwave.invenflow.portal.web

import org.bruwave.invenflow.commons.YamlPropertySourceFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

// @EnableWebMvc
@Configuration
@EnableAutoConfiguration
@ComponentScan(
    basePackages = ["org.bruwave.invenflow.portal.web"],
)
@PropertySource("classpath:application-web.yml", factory = YamlPropertySourceFactory::class)
class WebContentConfiguration
