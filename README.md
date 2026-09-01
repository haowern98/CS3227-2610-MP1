# Possession Manager

Possession Manager is a local JavaFX desktop application for tracking physical possessions, their
lifecycle history, location, current status, tags, and notes. It stores data locally as JSON and
does not require an account or online service.

## Run the release

Use a Java 25 JDK. From the repository root, run:

```bash
java -jar release/PossessionManager.jar
```

## Develop from source

The Gradle wrapper is for building and testing the source code. Run automated checks with
`./gradlew clean check javadoc assemble` on macOS or Linux, or with
`.\gradlew.bat clean check javadoc assemble` on Windows.

See the [User Guide](docs/UserGuide.md) for application instructions and the
[Developer Guide](docs/DeveloperGuide.md) for the design, build, and testing approach.
