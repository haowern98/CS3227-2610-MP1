# Possession Manager

Possession Manager is a local JavaFX desktop application for tracking physical possessions, their
lifecycle history, location, current status, tags, and notes. It stores data locally as JSON and
does not require an account or online service.

## Run

Use a Java 25 JDK and run the Gradle wrapper from the repository root.

Windows:

```powershell
.\gradlew.bat run
```

macOS or Linux:

```bash
./gradlew run
```

Run the automated checks with `./gradlew clean check javadoc assemble` on macOS or Linux, or with
`.\gradlew.bat clean check javadoc assemble` on Windows.

See the [User Guide](docs/UserGuide.md) for full instructions and the
[Developer Guide](docs/DeveloperGuide.md) for the design and testing approach.
