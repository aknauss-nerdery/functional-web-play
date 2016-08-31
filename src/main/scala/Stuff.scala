package thing

import play.api.libs.json.Json

import scala.util.{Failure, Success, Try}

/**
  * Created by aknauss on 8/30/16.
  */
case class Stuff(vals: Seq[Int])

case class StuffDeserializer() extends Deserializer[Stuff] {
  implicit val tReads = Json.reads[Stuff]
}

case class StuffSerializer() extends Serializer[Stuff] {
  implicit val tWrites = Json.writes[Stuff]
}

case class StuffService() extends Service[Stuff, Thing] {

  def validate(t: Stuff, metadata: Thing): Try[Stuff] = Try {
    assert(metadata.value == 1)
    t
  }

}

case class StuffController(deserializer: Deserializer[Stuff],
                           metadataRepository: Repository[Thing],
                           service: Service[Stuff, Thing],
                           repository: Repository[Stuff],
                           serializer: Serializer[Stuff]) extends Controller[Stuff] {
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
  def insert(request: JsonRequest[Stuff]): Response =
    metadataRepository.get(request)
      .flatMap(metadata => deserializer.deserialize(request).map((metadata, _)))
      .flatMap { case (metadata, t) => service.validate(t, metadata) }
      .flatMap(repository.insert _)
      .flatMap(serializer.serialize(_)) match {
      case Success(response) => response
      case Failure(e) => ErrorResponse(e)

    }
}
