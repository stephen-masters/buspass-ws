Bus pass web service
====================

A minimal Drools web service on Spring Boot. It does nothing more than expose a set of Drools
rules as an HTTP API, so that the essentials of running Drools inside a Spring Boot application
are easy to pick out.

I wrote it after [Qzr](https://github.com/stephen-masters/qzr), which showed Spring Boot with Drools but
also brought in AngularJS and Spring HATEOAS, and was a bit much for someone who only wanted to
see the rules engine behind a web service. The rules take their cue from the
[bus pass example](https://github.com/apache/incubator-kie/tree/main/drools-examples/src/main/java/org/drools/examples/buspass)
in the Drools project, cut down a little, with the facts that only the rules refer to replaced by
DRL declared types.

| Piece | Where | What it does |
|---|---|---|
| Rules | `src/main/resources/com/sctrcd/buspassws/rules/BusPass.drl` | Under 16 gets a `ChildBusPass`, 16 and over an `AdultBusPass` |
| Knowledge base | `src/main/resources/META-INF/kmodule.xml` | Declares `BusPassKbase` and the `BusPassSession` session |
| Container | `BusPassApp` | The `KieClasspathContainer` as a Spring bean |
| Service | `BusPassService` | A new session per request: insert the person, fire the rules, find the pass |
| Controller | `BusPassController` | `GET /buspass?name=…&age=…` |

It needs Java 25 and uses Spring Boot 4 with Drools 10. The Maven wrapper downloads Maven itself,
so to run a full build, including the format check and the tests:

    ./mvnw clean verify

Run the application:

    java -jar target/buspass-ws-1.0.0-SNAPSHOT.jar

Then send a request with curl or a browser. Opening
<http://127.0.0.1:8080/buspass?name=Steve&age=15> gives:

    {"person":{"name":"Steve","age":15},"busPassType":"ChildBusPass"}

and <http://127.0.0.1:8080/buspass?name=Steve&age=16> gives:

    {"person":{"name":"Steve","age":16},"busPassType":"AdultBusPass"}

A request without `name` or `age`, or with an age that is not a whole number, gets a
`400 Bad Request`. Spring Boot Actuator's health check is at <http://127.0.0.1:8080/actuator/health>.

If the format check fails, `./mvnw spotless:apply` fixes it. To install the git hooks, which
check formatting before each commit and run the build before each push:

    lefthook install

See [AGENTS.md](AGENTS.md) for the conventions and [SECURITY.md](SECURITY.md) for reporting a
vulnerability.

Licensed under the [Apache License, Version 2.0](LICENSE).
