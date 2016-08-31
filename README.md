# Function Web Example

This is a quick example of building controller, service, repository, and (de)serialization functionality in a more functional way.

## Building
`sbt compile`

## Running
`sbt run`

## Output
Right now the project just hits the `insert` endpoints for each domain object with a dummy object and prints out the returned result.

## Files
- Main.scala - Entrypoint. Sets up some fake dependencies and then calls some controller endpoints.
- Lib.scala - Contains reusable traits / parents for all of the related types. The bulk of this example lives in this file. 
- Thing.scala - Example of specializing the constructs in `Lib.scala` to provide endpoints for interacting with domain objects of type `Thing`.
- Stuff.scala - Example of specializing the constructs in `Lib.scala` to provide endpoints for interacting with domain objects of type `Stuff`.

## TODO
- Expand the service, repository, and controller layers into a full CRUD example
- Integrate with Play framework, replacing Main.scala with Play application hosting
- Integrate with a persistence solution / database
