package quo.vadis.megasys.utils.keycloak

import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

data class KeycloakGrantedAuthority(
  private val authority: GrantedAuthority,
  val type: String,
  val id: String? = null
) : GrantedAuthority by authority {
  fun isRealmRole(): Boolean = (KeycloakAuthenticationConverter.ATTR_REALM_ACCESS == type)
  fun isResourceRole(): Boolean = (KeycloakAuthenticationConverter.ATTR_RESOURCE_ACCESS == type)
}

class KeycloakAuthenticationConverter : JwtAuthenticationConverter() {
  companion object {
    const val ATTR_REALM_ACCESS = "realm_access"
    const val ATTR_RESOURCE_ACCESS = "resource_access"
    const val ATTR_ROLES = "roles"
    const val ATTR_PREFERRED_USERNAME = "preferred_username"
    /**
     * 名
     */
    const val ATTR_GIVEN_NAME = "given_name"
    /**
     * 性
     */
    const val ATTR_FAMILY_NAME = "family_name"
    /**
     * W式の名前
     */
    const val ATTR_NAME = "name"

    fun jwtAuthenticationTokenFrom(auth: Authentication): JwtAuthenticationToken? {
      return auth as? JwtAuthenticationToken
    }

    fun jwtFrom(auth: Authentication): Jwt? {
      return jwtAuthenticationTokenFrom(auth)?.token
    }

    fun preferredUsernameFrom(auth: Authentication): String? {
      return jwtAuthenticationTokenFrom(auth)?.tokenAttributes?.get(ATTR_PREFERRED_USERNAME) as? String
    }

    fun givenNameFrom(auth: Authentication): String? {
      return jwtAuthenticationTokenFrom(auth)?.tokenAttributes?.get(ATTR_GIVEN_NAME) as? String
    }

    fun familyNameFrom(auth: Authentication): String? {
      return jwtAuthenticationTokenFrom(auth)?.tokenAttributes?.get(ATTR_FAMILY_NAME) as? String
    }

    fun nameFrom(auth: Authentication): String? {
      return jwtAuthenticationTokenFrom(auth)?.tokenAttributes?.get(ATTR_NAME) as? String
    }

    fun hasRealmRole(auth: Authentication, role: String): Boolean {
      return auth.authorities
        .mapNotNull { it as? KeycloakGrantedAuthority }
        .firstOrNull {
          (it.authority == role) && (it.type == ATTR_REALM_ACCESS)
        }
        ?.let { true }
        ?: false
    }

    fun hasResourceRole(auth: Authentication, role: String, resource: String): Boolean {
      return auth.authorities
        .mapNotNull { it as? KeycloakGrantedAuthority }
        .firstOrNull {
          (it.authority == role) && (it.type == ATTR_RESOURCE_ACCESS) && (it.id == resource)
        }
        ?.let { true }
        ?: false
    }
  }

  /**
   * jwt中のロールをGrantedAuthorityに変換。
   */
  override fun extractAuthorities(jwt: Jwt): Collection<GrantedAuthority> {
    val realmAuthorities = ((jwt.claims[ATTR_REALM_ACCESS] as? JSONObject)?.get(ATTR_ROLES) as? JSONArray)
      ?.map { it.toString() }
      ?.map { KeycloakGrantedAuthority(SimpleGrantedAuthority(it), ATTR_REALM_ACCESS) }
      ?.toList()
      ?: emptyList()

    val resourceAuthorities = (jwt.claims[ATTR_RESOURCE_ACCESS] as? JSONObject)
      ?.flatMap { entry ->
        ((entry.value as? JSONObject)?.get(ATTR_ROLES) as? JSONArray)
          ?.map { it.toString() }
          ?.map { KeycloakGrantedAuthority(SimpleGrantedAuthority(it), ATTR_RESOURCE_ACCESS, entry.key) }
          ?.toList()
          ?: emptyList()
      }?.toList()
      ?: emptyList()

    return realmAuthorities.plus(resourceAuthorities)
  }
}
