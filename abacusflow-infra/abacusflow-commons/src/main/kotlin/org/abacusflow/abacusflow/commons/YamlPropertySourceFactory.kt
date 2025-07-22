package org.abacusflow.commons

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory
import java.io.IOException
import java.util.Properties

class YamlPropertySourceFactory : PropertySourceFactory {
    @Throws(IOException::class)
    override fun createPropertySource(
        name: String?,
        encodedResource: EncodedResource,
    ): PropertySource<*> {
        val factory = YamlPropertiesFactoryBean()
        factory.setResources(encodedResource.resource)
        factory.afterPropertiesSet() // 显式调用初始化（可选但推荐）

        val properties: Properties = factory.`object`!!
        val resourceName = encodedResource.resource.filename ?: "yaml-config"

        return PropertiesPropertySource(resourceName, properties)
    }
}
