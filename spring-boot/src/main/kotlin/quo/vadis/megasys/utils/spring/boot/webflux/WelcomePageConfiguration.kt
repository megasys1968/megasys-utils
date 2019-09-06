package quo.vadis.megasys.utils.spring.boot.webflux

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.core.io.ResourceLoader
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.result.method.RequestMappingInfo
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import kotlin.reflect.jvm.javaMethod
import quo.vadis.megasys.utils.spring.boot.AbstractWelcomePageConfiguration

@Controller
class WelcomePageConfiguration(resourceProperties: ResourceProperties, resourceLoader: ResourceLoader, val mapping: RequestMappingHandlerMapping)
  : AbstractWelcomePageConfiguration(resourceProperties, resourceLoader), InitializingBean {

  @Value("\${megasys.utils.spring.boot.welcome-page:/**/{path:[^.]+}}")
  lateinit var welcomePagePaths: Array<String>

  override fun afterPropertiesSet() {
    if (null != welcomePage) {
      mapping.registerMapping(
        RequestMappingInfo.paths(*welcomePagePaths).methods(RequestMethod.GET)
          .produces(MediaType.TEXT_HTML_VALUE).build(),
        this, WelcomePageConfiguration::welcome.javaMethod!!)
    }
  }

  fun welcome(): Mono<ResponseEntity<*>> {
    return Mono.just(welcomePage
      ?.let {
        ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(welcomePage)
      }
      ?: ResponseEntity.notFound().build<Any>())
  }
}
