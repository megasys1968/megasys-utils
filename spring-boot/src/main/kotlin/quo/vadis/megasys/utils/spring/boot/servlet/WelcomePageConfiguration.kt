package quo.vadis.megasys.utils.spring.boot.servlet

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver
import quo.vadis.megasys.utils.spring.boot.AbstractWelcomePageConfiguration

@ConditionalOnProperty(value = ["megasys.utils.spring.boot.welcome-page.enable"], havingValue = "true", matchIfMissing = true)
@Configuration
class WelcomePageConfiguration(val resourceProperties: ResourceProperties, resourceLoader: ResourceLoader) : AbstractWelcomePageConfiguration(resourceProperties, resourceLoader), WebMvcConfigurer {

  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    welcomePage
      ?.also { resource ->
        registry.addResourceHandler("/**/*")
          .addResourceLocations(*resourceProperties.staticLocations)
          .resourceChain(true)
          .addResolver(object : PathResourceResolver() {
            override fun getResource(resourcePath: String, location: Resource): Resource? {
              val requestedResource = location.createRelative(resourcePath)

              return if (requestedResource.exists() && requestedResource.isReadable) {
                requestedResource
              } else {
                // 最後の静的リソースロケーションで、リソースが見つからなかったならwelcomePageを返却。
                if (resourceProperties.staticLocations.last() == location.url.toString()) {
                  resource
                } else {
                  null
                }
              }
            }
          })
      }
  }
}
