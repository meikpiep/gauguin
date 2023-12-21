# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

- Changed appearance of cells dealing with cheating and errors:
  - Revealed cells appear in gray scale.
  - If a cage a filled but does not match its maths result, all cells get an red background and the cage gets a red outline.
  - If two cells in a row or column get filled with the same value, both cells get a red background.
  - When using the menu item 'Show errors', all wrongly filled cells get a red background and the value is displayed in a red color, too.
  This should clearify if a cell was revealed (greyscale), if there is an error regarding the cell (red background) or if the value itself is wrong (red value).

### Deprecated

### Removed

### Fixed

- Fixed 'Show errors' which did not work at all.

### Security

## [0.16.0] - 2023-12-20

### Added

- Add option to display number in other number systems as the usual decimal system. Enables using
  number systems with a base of 2, 4, 8, 10 and 16. The cage text now spans over the hole cage if
  needed. Furthermore, the cage text got an adaptive size. When playing with huge numbers like the
  Fibonacci sequence, there is a good chance that not all numbers are readable on a classic phone
  screen.
- Add separate tab containing options regarding to numbers in new game screen.
- Add badges to tabs in new game screen indicating if the tab contains options which were moved from
  the default options.
- Add confirmation dialog when resetting statistics.

### Changed

- Rework light theme to use the colors of Gauguin more directly and enhance readability.
- Optimize Fast Finishing Mode:
  - Only activate it if there are cells with a single pencil mark left.
  - Exit the mode as soon as there is no cell with a single pencil mark.

### Fixed

- When revealing a cell or cage, the pencil marks get updated, too.
- Avoids breaking the streak if a new game has not been played yet.
- When playing with a tablet sized device in landscape mode, the app bar did not use the full width of the screen.

## [0.15.1] - 2023-12-11

### Fixed

- Fix bug where the top area of the main screen is squeezed to the right and thus unreadable.
- A restarted game now no longer shows a non-zero count of mistakes.
- A new game now no longer shows a non-zero number of filled cells and mistakes in rare circumstances.

## [0.15.0] - 2023-12-06

### Added

- Beta feature: 'Fast Finishing Mode' to rapidly fill cells at the end of the game. This is an opt-in beta feature, feel free to try out via preferences and report any feedback.
  The core feature (rapidly entering numbers) should already work, the mechanism to activate it is still in flow. Currently, entering three single pencil marks via long tapping consecutively will activate it.
  The ui elements blocking the keypad are far from finished, but functional and highly visible.
  The grid ui with yellow contrast color will likely not be changed.
- Add a bunch of calculated difficulties to let the user choose which difficulty he/she wants to play.
  Calculated til now: All square game variants up to 9x9 with advanced settings untouched.

## [0.14.0] - 2023-11-27

### Changed

- Optimize layout of both the main screen and the new game screen. The underlying variants of the layouts stay the same, but there should be now a better chance to display a consistent layout which matches the current window size.
- Optimize scrolling in new game screen.

### Fixed

- Fix layout of the main screen when using a 8' tablet with aspect ratio of e.g. 16:10.

## [0.13.1] - 2023-11-24

### Added

- Add translation of one forgotten string.

### Fixed

- Fix cages with division and result '0' to be always shown as filled wrong

## [0.13.0] - 2023-11-21

### Changed

- UI optimization: Change position of '0' on the key pad to only be positioned as the last number if it is the first and lowest possible number. This retains 1, 2, 3 as the first row of numbers.

### Fixed

- Fix statistics about started, hinted and solved puzzles which were stuck at 0.
- Avoid showing the "undo" option while displaying the errors if there is no error or no move to be undone.
- Fix behavior of the "undo" option while displaying the errors to behave completly the same as use the undo button of the app bar.
- Fix padding of the grid on the new game screen.

## [0.12.1] - 2023-11-18

As Google play refuses to accept this version code after an aborted creation of a release, this is technically the same as 0.12.0 with an increased versin code and version number.

## [0.12.0] - 2023-11-18 [YANKED]

### Added

- Add German localization.

## [0.11.0] - 2023-11-18

### Added

- Add monochrome launcher icon to be used with e.g. Android 13 and Themed Icons.

### Fixed

- Possible numbers were overlapping with other UI elements and popping out of the cells bounds.
- If there are two values entered in one row or column, only count the wrong value as a single mistake.
- When revealing the value of a cell, the possible numbers of this cell are now cleared.
- Revealing of selected cell or selected cage was not working reliable.

## [0.10.1] - 2023-11-12

### Fixed

- Fix bug where the difficulty of game variants may not be chosen even thought the difficulty is known of the game variant.
- Fix bug where a restart of a game does not clear the possible numbers.
- Fix layout bug where the main screen and other screens were using a tablet like layout but being modern smartphones having >480dp wide displays.

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
