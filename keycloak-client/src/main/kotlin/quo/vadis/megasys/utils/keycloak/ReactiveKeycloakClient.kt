package quo.vadis.megasys.utils.keycloak

import org.keycloak.OAuth2Constants
import org.keycloak.representations.AccessTokenResponse
import org.keycloak.representations.account.UserRepresentation
import org.keycloak.representations.idm.MappingsRepresentation
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import quo.vadis.megasys.utils.logger
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.extra.processor.TopicProcessor

class ReactiveKeycloakClient(val webClient: WebClient, val realm: String, val client: String) {
  companion object {
    val log = logger()
  }

  fun accessToken(username: String, password: String) : Mono<AccessTokenResponse> {
    return webClient.post().uri("/realms/$realm/protocol/openid-connect/token")
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .body(BodyInserters
        .fromFormData(OAuth2Constants.CLIENT_ID, client)
        .with("username", username)
        .with("password", password)
        .with(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD)
      )
      .retrieve()
      .bodyToMono<AccessTokenResponse>()
      .doOnError{
        KeycloakAdminCliClient.log.error("accessToken fail", it)
      }
  }
}

class ReactiveKeycloakAdminCliClient(val webClient: WebClient) {
  companion object {
    val log = logger()
  }

  fun accessToken(username: String, password: String) : Mono<AccessTokenResponse> {
    return ReactiveKeycloakClient(webClient, "master", "admin-cli").accessToken(username, password)
  }

  fun userRepresentation(token: String, realm: String) : Flux<UserRepresentation> {
    val processor = TopicProcessor.create<Int>()
    val sink = processor.sink()
    val pageSize = 50
    sink.next(0)
    return processor
      .flatMap {offset ->
        webClient.get()
          .uri{
            it.path("/admin/realms/$realm/users")
              .queryParam("first", offset)
              .queryParam("max", pageSize)
              .build()
          }
          .headers {
            it.setBearerAuth(token)
          }
          .retrieve()
          .bodyToMono<List<UserRepresentation>>()
          .doOnError{
            log.error("userRepresentation", it)
          }
          .flatMapMany {users ->
            if (users.size < pageSize) {
              sink.complete()
            } else {
              sink.next(offset + pageSize)
            }
            Flux.fromIterable(users)
          }
      }
  }

  fun mappingsRepresentation(token: String, realm: String, userId: String) : Mono<MappingsRepresentation> {
    return webClient.get().uri("/admin/realms/$realm/users/$userId/role-mappings")
      .headers {
        it.setBearerAuth(token)
      }
      .retrieve()
      .bodyToMono<MappingsRepresentation>()
      .doOnError{
        log.error("mappingsRepresentation", it)
      }
  }
}
