package quo.vadis.megasys.utils.graphql

import graphql.ExecutionInput
import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import graphql.spring.web.reactive.ExecutionInputCustomizer
import graphql.spring.web.reactive.components.GraphQLController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


const val GQL_CONTEXT_AUTH_KEY = "authentication"
const val GQL_CONTEXT_ROLES_KEY = "roles"

fun graphQLContextFrom(env: DataFetchingEnvironment): GraphQLContext? {
  return env.getContext<Any>() as? GraphQLContext
}

fun authenticationFrom(env: DataFetchingEnvironment): Authentication? {
  return graphQLContextFrom(env)?.get<Any>(GQL_CONTEXT_AUTH_KEY) as? Authentication
}

fun rolesFrom(env: DataFetchingEnvironment): List<String>? {
  return graphQLContextFrom(env)?.get<Any>(GQL_CONTEXT_ROLES_KEY) as? List<String>
}

/**
 * graphql-javaのGraphQLContextに、spring-securityの認証認可情報を設定する
 * graphql-java-spring-webflux用の拡張コンポーネント
 */
class SecurityExecutionInputCustomizer: ExecutionInputCustomizer {
  override fun customizeExecutionInput(
    executionInput: ExecutionInput,
    webRequest: ServerWebExchange
  ): Mono<ExecutionInput> {
    return ReactiveSecurityContextHolder.getContext()
      .map { it.authentication }
      .doOnNext { auth ->
        (executionInput.context as? GraphQLContext)?.let { qlContext ->
          qlContext.put(GQL_CONTEXT_AUTH_KEY, auth)
          qlContext.put(GQL_CONTEXT_ROLES_KEY, auth.authorities?.map {
            it.authority
          }?.toList() ?: emptyList<String?>())
        }
      }
      .map { executionInput }
  }
}

/**
 * graphql-java-spring-webfluxのパッケージをComponentScanすることで
 * 同パッケージに存在する何もしないExecutionInputCustomizerを
 * GraphQLControllerが使用するのを回避する
 */
@Configuration
@ComponentScan(basePackageClasses = [GraphQLController::class], excludeFilters = [
  ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [ExecutionInputCustomizer::class])
])
class GraphQlSecurityConfiguration {
  @Bean
  fun executionInputCustomizer(): ExecutionInputCustomizer {
    return SecurityExecutionInputCustomizer()
  }
}