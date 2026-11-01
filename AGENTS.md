# AGENTS.md

Guidance for anyone working in this repository with a coding agent (Claude
Code, Codex, Cursor, Gemini CLI or whatever else reads this file) and a fair
summary of the house rules for contributors who use none.

## What this repository is

**buspass-ws** is a minimal Drools web service on Spring Boot. It is a
teaching example, not a library or an application: nothing is released or
deployed, and the code should stay as short as the point it makes.

The point is this. Drools finds `META-INF/kmodule.xml` on the classpath,
which declares a knowledge base built from the rules in one package and a
named session over it. A `KieContainer` for the classpath is a Spring bean;
the service asks it for a new `BusPassSession` per request, inserts a
`Person`, fires the rules and returns whichever `BusPass` they inserted. The
blog post
[A minimal Spring Boot Drools web service](https://scattercode.dev/2015/02/a-minimal-spring-boot-drools-web-service/)
walks through it.

## Hard rules

### Keep it minimal

Anything that is not needed to show Drools behind a Spring Boot endpoint is
out of scope. Do not add persistence, security, a UI or extra endpoints.

### Keep the API the post describes

`GET /buspass?name=…&age=…` returns
`{"person":{"name":"Steve","age":15},"busPassType":"ChildBusPass"}`, and
`AdultBusPass` from 16. The controller tests assert those bodies; do not
weaken them to make a change pass.

### The rules package must match the knowledge base

The `packages` attribute of `BusPassKbase` in `kmodule.xml` is matched
against the `package` line of the DRL, not only its folder. Rename one and
the knowledge base silently comes up empty. `BusPassRulesTest` catches that.

### Stay on current LTS versions

The project tracks the latest LTS JDK (`java.version` in `pom.xml`, and
`java-version` in `.github/workflows/ci.yml`; change both together) and the
latest Spring Boot GA release. Dependency versions come from the Spring Boot
parent's dependency management, and the Drools and KIE versions from the
Drools BOM (`drools.version`): do not pin a version that either already
manages.

## Layout

| Path | What |
|---|---|
| `src/main/java/.../BusPassApp.java` | `@SpringBootApplication`; the `KieContainer` bean |
| `src/main/java/.../BusPassService.java` | A session per request; finds the bus pass the rules inserted |
| `src/main/java/.../BusPassController.java` | `GET /buspass` |
| `src/main/java/.../facts/` | `Person` and the `BusPass` hierarchy |
| `src/main/resources/META-INF/kmodule.xml` | The knowledge base and session |
| `src/main/resources/.../rules/BusPass.drl` | The rules, with the `IsChild` and `IsAdult` declared types |
| `src/test/java/.../BusPassRulesTest.java` | The rules in a bare `KieSession`, including truth maintenance |
| `src/test/java/.../BusPassWebServiceTest.java` | The Spring-wired service |
| `src/test/java/.../BusPassControllerTest.java` | The HTTP API through MockMvc |
| `.githooks/commit-msg` | Conventional Commits check, shared from `scattercode/release-pipelines` |

## Checks

```bash
./mvnw spotless:apply   # format the Java sources and the POM
./mvnw verify           # everything below, and the tests
```

`./mvnw verify` runs, in order:

- the **enforcer**: the JDK is at least `java.version`, Maven is at least
  3.9, and dependency versions converge;
- the **Spotless** check (palantir-java-format for Java, sortpom for
  `pom.xml`) in the `validate` phase;
- the **compiler** with `-Xlint:all -Werror`, so any warning fails the build;
- the **tests**.

It needs JDK 25; the Maven wrapper fetches Maven itself. All of it must
pass before a commit. The lefthook hooks run the format check before each
commit and the full build before each push; CI runs the build and a Trivy
scan on every push and pull request.

## Conventions

- British English; sentence case for headings.
- Conventional Commits, enforced by `.githooks/commit-msg` and, for pull
  request titles, by `pr-title.yml`. Dependency updates are `build:`,
  workflow changes `ci:`.
- `.githooks/commit-msg` is a byte-for-byte copy of the template in
  `scattercode/release-pipelines`; CI checks it has not drifted. Change it
  there, not here.
- `main` is protected: changes arrive through pull requests with the CI
  checks passing.
- Hook setup in a fresh clone: `lefthook install`, or without lefthook,
  `git config core.hooksPath .githooks` for the commit message check alone.
