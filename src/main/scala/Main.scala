package thing

import java.io.InvalidObjectException

object Main extends App {

  override def main(args: Array[String]): Unit = {

    // Thing dependencies, to inject with Guice
    val thingDeserializer = ThingDeserializer()
    val metadataRepository = Repository[Int](request => 42)
    val thingService = ThingService()
    val thingRepository = Repository[Thing](_ => Thing(1,2))
    val thingSerializer = ThingSerializer()
    val thingController = ThingController(thingDeserializer,
                                          metadataRepository,
                                          thingService,
                                          thingRepository,
                                          thingSerializer)

    val stuffDeserializer = StuffDeserializer()
    val stuffService = StuffService()
    val stuffRepository = Repository[Stuff](_ => throw new InvalidObjectException("Not provided"))
    val stuffSerializer = StuffSerializer()
    val stuffController = StuffController(stuffDeserializer,
      thingRepository,
      stuffService,
      stuffRepository,
      stuffSerializer)


    println(thingController.insert(JsonRequest[Thing]("{\"value\":555,\"value2\":11}")))
    println(stuffController.insert(JsonRequest[Stuff]("{\"vals\":[7,14,21]}")))
  }

}