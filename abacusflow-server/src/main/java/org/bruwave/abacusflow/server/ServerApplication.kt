package org.bruwave.abacusflow.server

import org.bruwave.abacusflow.portal.web.WebContentConfiguration
import org.bruwave.abacusflow.usecase.CoreContextConfiguration
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .sources(CoreContextConfiguration::class.java)
//        .bannerMode(Banner.Mode.OFF)
        .web(WebApplicationType.NONE)
        .child(WebContentConfiguration::class.java)
        .web(WebApplicationType.SERVLET)
        .run(*args)
}
