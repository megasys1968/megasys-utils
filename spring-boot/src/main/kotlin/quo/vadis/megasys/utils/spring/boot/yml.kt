package quo.vadis.megasys.utils.spring.boot

import org.springframework.boot.context.properties.bind.Binder
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource
import org.springframework.core.io.Resource

inline fun <reified T> loadFromYml(resource: Resource, propertyName: String): T? {
  return YamlPropertiesFactoryBean()
    .apply {
      setResources(resource)
    }
    .getObject()
    ?.let {
      val propSource = MapConfigurationPropertySource(it)
      val binder = Binder(propSource)
      binder.bind(propertyName, T::class.java).get()
    }
}