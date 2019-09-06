package quo.vadis.megasys.utils.keycloak

import org.keycloak.OAuth2Constants
import org.keycloak.representations.AccessTokenResponse
import org.keycloak.representations.account.UserRepresentation
import org.keycloak.representations.idm.MappingsRepresentation
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import quo.vadis.megasys.utils.logger

class KeycloakClient(val baseUrl: String, val realm: String, val client: String, val webClient: RestTemplate = RestTemplate()) {
  fun accessToken(username: String, password: String): AccessTokenResponse? {

    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

    val body = LinkedMultiValueMap<String, String>()
    body.add(OAuth2Constants.CLIENT_ID, client)
    body.add("username", username)
    body.add("password", password)
    body.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD)

    val request = HttpEntity(body, headers)

    return webClient.exchange("$baseUrl/realms/$realm/protocol/openid-connect/token", HttpMethod.POST, request, AccessTokenResponse::class.java).body
  }
}

class KeycloakAdminCliClient(val baseUrl: String, val webClient: RestTemplate = RestTemplate()) {
  companion object {
    val log = logger()
    const val PAGE_SIZE = 100
  }

  fun accessToken(username: String, password: String): AccessTokenResponse? {
    return KeycloakClient(baseUrl, "master", "admin-cli", webClient).accessToken(username, password)
  }

  fun userCount(token: String, realm: String): Int {
    val headers = HttpHeaders()
    headers.setBearerAuth(token)
    val request = HttpEntity<MultiValueMap<String, String>>(headers)
    return webClient.exchange(
      "$baseUrl/admin/realms/$realm/users/count",
      HttpMethod.GET, request, Int::class.java)
      .body ?: throw RuntimeException("応答なしエラー")
  }

  fun userRepresentation(token: String, realm: String): Collection<UserRepresentation> {
    var userCount = userCount(token, realm)
    log.info("ユーザ件数: $userCount")
    val headers = HttpHeaders()
    headers.setBearerAuth(token)
    val request = HttpEntity<MultiValueMap<String, String>>(headers)
    var offset = 0
    val users = mutableListOf<UserRepresentation>()

    do {
      val response = webClient.exchange(
        "$baseUrl/admin/realms/$realm/users?first=$offset&max=$PAGE_SIZE",
        HttpMethod.GET, request, object : ParameterizedTypeReference<List<UserRepresentation>>() {})
        .body
        ?: listOf()
      users.addAll(response)
      offset += response.size
      log.info("ユーザ取得件数: $offset / $userCount")

      // 全ユーザ取得前に応答が0件
      if (response.isEmpty() && (offset < userCount)) {
        val retryUserCount = userCount(token, realm)
        log.info("再取得ユーザ件数: $retryUserCount")
        // 最初からやり直し
        users.clear()
        userCount = retryUserCount
        offset = 0
      }
    } while (offset < userCount)
    return users
  }

  fun mappingsRepresentation(token: String, realm: String, userId: String): MappingsRepresentation? {
    val headers = HttpHeaders()
    headers.setBearerAuth(token)
    val request = HttpEntity<MultiValueMap<String, String>>(headers)
    return webClient.exchange("$baseUrl/admin/realms/$realm/users/$userId/role-mappings",
      HttpMethod.GET, request, MappingsRepresentation::class.java).body
  }
}
