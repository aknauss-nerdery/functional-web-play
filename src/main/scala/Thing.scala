package thing

import play.api.libs.json.Json

import scala.util.{Failure, Success, Try}

/**
  * Created by aknauss on 8/30/16.
  */
case class Thing(value: Int, value2: Int)

case class ThingDeserializer() extends Deserializer[Thing] {
  implicit val tReads = Json.reads[Thing]
}

case class ThingSerializer() extends Serializer[Thing] {
  implicit val tWrites = Json.writes[Thing]
}

case class ThingService() extends Service[Thing, Int] {

  def validate(t: Thing, metadata: Int): Try[Thing] = Try {
    assert(metadata == 42)
    t
  }

}

case class ThingController(deserializer: Deserializer[Thing],
                           metadataRepository: Repository[Int],
                           service: Service[Thing, Int],
                           repository: Repository[Thing],
                           serializer: Serializer[Thing]) extends Controller[Thing] {
  /**
    * Endpoint for handling insertThing requests.
    *
    * It's worth noting here that the entire implementation is one call chain (no {}). Since the only
    * thing this function should be doing is mapping from Request through transformations, to Response,
    * that is what we expect.
    *
    * @param request A request assumed to have its body be a Thing representation.
    *                In the real world this would likely be a view type, and our deserializer would
    *                handle mapping to the domain (or have separate serializer/toDomain steps)
    * @return Either a response representing the success or failure result
    */
  def insert(request: JsonRequest[Thing]): Response =
    metadataRepository.get(request)
      .flatMap(metadata => deserializer.deserialize(request).map((metadata, _)))
      .flatMap { case (metadata, t) => service.validate(t, metadata) }
      .flatMap(repository.insert _)
      .flatMap(serializer.serialize(_)) match {
      case Success(response) => response
      case Failure(e) => ErrorResponse(e)
    }
}
