package quo.vadis.megasys.utils.jsoup
import org.jsoup.Connection

fun Connection.execute(cookieHolder: MutableMap<String, String>): Connection.Response {
  this.cookies(cookieHolder)
  val response = this.execute()
  cookieHolder.putAll(response.cookies())
  return response
}
