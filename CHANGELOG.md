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

- Fix bug where the difficulty of game variants may not be choosed even thought the difficulty is known of the game variant.

### Security

## [0.10.0] - 2023-11-06

Bumps used Java version from 1.8 to 11 as needed by Material Drawer from Mike Penz.

### Added

- Show drawing 'The Siesta' from Paul Gauguin when opening the navigation drawer.

### Changed

- Use third party navigation drawer, enabling badges to display the number of saved games.

### Deprecated

### Removed

### Fixed

- Fix new preference values which were ignored. From now on, dark mode gets activated and the settings of possible numbers are changed.
- Fix initial help dialog was not showing up.

### Security

## [0.9.4] - 2023-10-31

### Changed

- Update forgotten version number to 0.9.4 and version code to 4

## [0.9.3] - 2023-10-31

### Security

- Use matching version of Gradle wrapper jar and ensure that the wrapper in use matches the SHA256 hash of the original one. 

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
