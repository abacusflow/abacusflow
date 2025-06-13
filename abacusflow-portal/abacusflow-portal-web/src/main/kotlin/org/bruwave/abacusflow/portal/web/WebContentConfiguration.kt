package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.commons.YamlPropertySourceFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

// @EnableWebMvc
@Configuration
@EnableAutoConfiguration
@ComponentScan(
    basePackages = ["org.bruwave.abacusflow.portal.web"],
)
@PropertySource("classpath:application-web.yml", factory = YamlPropertySourceFactory::class)
class WebContentConfiguration
