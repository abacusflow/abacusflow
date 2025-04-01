package org.bruwave.invenflow.server

import org.bruwave.invenflow.portal.web.WebContentConfiguration
import org.bruwave.invenflow.usecase.CoreContextConfiguration
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder

class ServerApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .sources(CoreContextConfiguration::class.java)
//        .bannerMode(Banner.Mode.OFF)
        .web(WebApplicationType.NONE)
        .child(WebContentConfiguration::class.java)
        .web(WebApplicationType.SERVLET)
        .run(*args)
}
