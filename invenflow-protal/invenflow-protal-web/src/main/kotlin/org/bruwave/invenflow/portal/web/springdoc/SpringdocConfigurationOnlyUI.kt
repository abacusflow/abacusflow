// package org.bruwave.invenflow.portal.web.springdoc
//
// import org.bruwave.invenflow.portal.web.invoker.SpringDocConfiguration
// import org.springdoc.core.properties.SpringDocConfigProperties
// import org.springdoc.core.providers.ObjectMapperProvider
// import org.springframework.context.annotation.Bean
// import org.springframework.context.annotation.Configuration
//
//
// @Configuration
// class SpringdocConfigurationOnlyUI {
//    @Bean
//    fun springDocConfiguration(): SpringDocConfiguration {
//        return SpringDocConfiguration()
//    }
//
//    @Bean
//    fun springDocConfigProperties(): SpringDocConfigProperties {
//        return SpringDocConfigProperties()
//    }
//
//    @Bean
//    fun objectMapperProvider(springDocConfigProperties: SpringDocConfigProperties): ObjectMapperProvider {
//        return ObjectMapperProvider(springDocConfigProperties)
//    }
// }
class SpringdocConfigurationOnlyUI
