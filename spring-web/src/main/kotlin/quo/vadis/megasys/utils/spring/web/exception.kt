package quo.vadis.megasys.utils.spring.web

import org.springframework.http.HttpStatus

open class HttpStatusException(
  val status: HttpStatus,
  val detailMessage: String? = null,
  cause: Throwable? = null
) : RuntimeException(detailMessage?: status.reasonPhrase, cause)

class ForbiddenException(
  detailMessage: String? = null,
  cause: Throwable? = null)
  : HttpStatusException(HttpStatus.FORBIDDEN, detailMessage, cause)

class NotFoundException(
  detailMessage: String? = null,
  cause: Throwable? = null)
  : HttpStatusException(HttpStatus.NOT_FOUND, detailMessage, cause)

class BadRequestException(
  detailMessage: String? = null,
  cause: Throwable? = null)
  : HttpStatusException(HttpStatus.BAD_REQUEST, detailMessage, cause)

class PreconditionFailedException(
  detailMessage: String? = null,
  cause: Throwable? = null)
  : HttpStatusException(HttpStatus.PRECONDITION_FAILED, detailMessage, cause)

class InternalServerErrorException(
  detailMessage: String? = null,
  cause: Throwable? = null)
  : HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, detailMessage, cause)

class ServiceUnavailableErrorException(
  detailMessage: String? = null,
  cause: Throwable? = null)
  : HttpStatusException(HttpStatus.SERVICE_UNAVAILABLE, detailMessage, cause)
