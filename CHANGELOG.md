# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

- Use proper, versions of Gradle wrapper jar and ensure that the wrapper in use matches the SHA256 hash of the original one. 

## [0.9.2] - 2023-10-30

### Added

- Add changelog.

## [0.9.1] - 2023-10-29

### Added

- Add a privacy policy demanded by the Google play console.

### Removed

- Remove running SonarQube as it breaks the F-Droid build. Will be re-enabled soon.

## [0.9.0] - 2023-10-29

Initial release.

Main differences to HoloKenMod 1.6.1, from which this project was forked:
- Adds more grid options and more flexible grid sizes, including non-square grids
- Migrates code from Java to Kotlin
- Uses Android Material Components
- Overhauls complete UI overhaul, using theme based on a picture from Paul Gauguin
