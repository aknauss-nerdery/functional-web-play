package thing
/**
  * Example of a functional web framework setup
  */

import play.api.libs.json.Json
import play.api.libs.json.{Reads, Writes}

import scala.util.{Failure, Success, Try}

/**
  * Generic representation of a request, such as might be passed into a controller via REST call
  *
  * TODO: Use Play's request object instead
  *
  * @tparam TSerialized The type of the request's body
  */
trait Request[TSerialized] {
  val body: TSerialized
}

/**
  * Representation of a JSON request with the body containing a String representation of the Json for the type
  * @param body String JSON representation
  * @tparam T Scala case class type represented by the JSON
  */
case class JsonRequest[T](body: String) extends Request[String]

/**
  * Abstraction of taking a request of type TSerialized and converting it to the Scala case class type
  *
  * TODO: Separate generic Deserializer from Json deserializer
  *
  * @tparam T Scala case class type
  */
trait Deserializer[T] {
  implicit val tReads: Reads[T]
  def deserialize(request: JsonRequest[T]): Try[T] = Try(Json.parse(request.body).as[T])
}

/**
  * Representation of a business logic service. This is where your complicated business logic should live
  * (or, at least, the place where it's brought together.
  * @tparam T The type acted on by this service
  * @tparam TMetadata The type of metadata required by the service to resolve some logic
  */
trait Service[T, TMetadata] {
  def validate(t: T, metadata: TMetadata): Try[T]
}

/**
  * Representation of persisting data. Shouldn't leak implementation details.
  *
  * TODO: Implement this on an actual persistence mechanism
  * TODO: Remove the `provider` hack
  *
  * @param provider Hacky way of providing a dummy implementation
  * @tparam T The type acted on by this repository
  */
case class Repository[T](provider: Request[_] => T) {

  def insert(t: T): Try[T] =
    // TODO: Implement the side-effect storage
    Try(t)

  def get(request: Request[_]): Try[T] = Try(provider(request))
}

/**
  * Representation of a response object.
  *
  * TODO: Use, Play's response types instead.
  *
  */
sealed trait Response
case class DefaultResponse() extends Response
case class BodyResponse(body: String) extends Response
case class ErrorResponse(error: Throwable) extends Response

/**
  * Representation of serializing an object to a JSON string
  *
  * @tparam T the type of object to serialize
  */
trait Serializer[T] {
  implicit val tWrites: Writes[T]
  def serialize(t: T): Try[Response] = Try(Json.toJson(t)).map(_.toString).map(BodyResponse.apply _)
}

/**
  * Abstraction of MVC controller
  *
  * @tparam T Type of thing primarily acted upon by this controller
  */
trait Controller[T] {

  def insert(request: JsonRequest[T]): Response

  def simplestPossibleFunctionalEndpoint(request: Request[Any]): Response = DefaultResponse()

}
