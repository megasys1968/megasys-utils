package quo.vadis.megasys.utils.spring.boot

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.result.method.RequestMappingInfo
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import org.springframework.web.servlet.resource.PathResourceResolver
import quo.vadis.megasys.utils.logger
import quo.vadis.megasys.utils.spring.boot.webflux.WelcomePageConfiguration
import java.util.*
import kotlin.reflect.jvm.javaMethod

open class AbstractWelcomePageConfiguration(resourceProperties: ResourceProperties, resourceLoader: ResourceLoader) {
  companion object {
    val log = logger()
  }

  final val welcomePage: Resource? = resourceProperties.staticLocations
    .map {
      resourceLoader.getResource(StringUtils.appendIfMissing(it, "/") + "index.html")
    }
    .firstOrNull {
      it.exists()
    }

  init {
    if (null == welcomePage) {
      log.error("Welcome page not found.")
    }
  }
}
