package org.bruwave.abacusflow.usecase

import org.bruwave.abacusflow.commons.YamlPropertySourceFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableAutoConfiguration
@ComponentScan(
    basePackages = [
        "org.bruwave.abacusflow.usecase",
    ],
)
@EnableCaching
@EnableJpaRepositories(
    basePackages = [
        "org.bruwave.abacusflow.db",
    ],
)
@EntityScan(
    basePackages = [
        "org.bruwave.abacusflow.user",
        "org.bruwave.abacusflow.inventory",
        "org.bruwave.abacusflow.partner",
        "org.bruwave.abacusflow.product",
        "org.bruwave.abacusflow.transaction",
        "org.bruwave.abacusflow.depot",
    ],
)
@PropertySource("classpath:application-core.yml", factory = YamlPropertySourceFactory::class)
class CoreContextConfiguration
