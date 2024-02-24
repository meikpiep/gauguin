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

## [0.21.0] - 2024-02-24

### Added

- Added all difficulty levels of 6x6 grids.
- Adds a icon on the main screen to indicate if the current game values are not calculated via the
  usual decimal numeral system. The icon will show the larger digit of that numeral system, that is
  2, 4, 8 or F (indicating the hexadecimal system).

### Changed

- Re-implements the hint popup: It got more colorful and should indicate at first sight if there are
  any errors by different background color and icon. The popup is disposable via a single tap at
  any place on the screen.
- A long tap on a empty cell now copies common pencil marks of the cells in the same cage.
  Works on an empty cell (no value set and no pencil marks) if all non-empty pencil marks of the
  other cells of the cage contain the same or a similar set of pencil marks. 

### Fixed

- A solved game could not be restarted.
- Avoids statistics diagrams to show only a part of the data.

## [0.20.0] - 2024-02-06

### Added

- When solving a game, the keypad gets swapped by a card view which states the win of a game. It
  also shows if it was the first game solved ever, or the first kind been solved, or a best time
  which could be made.
- Added more detailed statistics. After solving at least one game, three diagrams visualize
  - The difficulty of the games solved.
  - The play time of the games solved.
  - The streak history of all games.
  There is currently no statistics by game variant. Feedback warmly welcome.

### Changed

- Move the popup messages of the main screen to the absolute bottom of the screen, now covering the
 bottom app bar. Avoids covering the bottom row of number buttons when checking if there are errors.
- Mark 'Fast Finishing Mode' as stable.
- Show hours of play time only if the game lasts longer than one hour.

### Removed

- Removed message when cheating/revealing cells. It seemed to be not motivating at all.
- Removed the possibility to shift the keypad from center to the left and right.

## [0.19.1] - 2024-01-23

### Fixed

- Avoid static color at main top frame when using dynamic colors.
- Fix pencils marks of new games when auto filling single cell cages.
- Fix restarted games to show recently modified cells of the previous game.

## [0.19.0] - 2024-01-21

### Added

- Added all difficulty levels of 4x4 and 5x5 grids.
- Added support of dynamic colors.
- Added new preference to set the maximum size of a grid cell, from 24 dp to 96 dp.

### Changed

- Converted images from PNG to WebP to reduce download and app size.

## [0.18.0] - 2024-01-05

### Added

- Added all difficulty levels of 3x3 grids.
- Added new preference to automatically fill single cages when starting a grid.

### Changed

- Limit maximum size of a grid cell to 96dp. Avoids overly huge cells.
- Grids now get saved via Yaml files. A migration of the old XML files will be triggered
  automatically when starting the app.
  This enables further development of grid types, removes a lot of legacy code, a relevant technical
  dept and makes the main developer sleep better.

### Removed

- Removed grid size 2. From now on, the minimum size of a grid is 3. This is due to three facts:
  - Not all variants of 2x2 grid could be calculated successfully.
  - The difficulty values of 2x2 grids were nearly all < 1.0 which would turn into misleading
    difficulty levels reaching from 0 to 0 or such things.
  - I think nobody actually plays such small grids.
  If you had faith in 2x2 grid, give me a ping and thus motivation to include such grids.

### Fixed

- Fixed load screen loading the wrong grid when clicking directly on the card surrounding the grid.
- When saving a grid and loading it, the current options where not saved until now. From now on, you
  can save a grid, go to the new game screen and alter any option you like. If you load your saved
  grid, all options where taken from that saved game. Previously, you could be stuck with e.g. a 6x6
  grid with the values 1..6, but altering the values between saving and loading to values 0..5 lead
  to a loaded grid, demanding values 1..6, but showing the key pad values 0..5.
- Loading a saved game via directly clicking on the grid area loaded the wrong game. 
- Fixed Fast Finishing Mode to display the value of the selected cell.
- Fixed grids showing binary, negative numbers to display proper values.
- Fixed old grid flashing when a new grid was started.

## [0.17.0] - 2024-01-02

### Added

- Added a popup displaying the difficulty levels and their respective boundaries. May be reached by
  - Tapping at the difficulty info at the top bar on the main screen.
  - Using the info icon near the difficulty choice in the new game screen.

### Changed

- Changed appearance of cells dealing with cheating and errors:
  - Revealed cells appear in gray scale.
  - If a cage a filled but does not match its math result, all cells get an red background and the cage gets a red outline.
  - If two cells in a row or column get filled with the same value, both cells get a red background.
  - When using the menu item 'Show errors', all wrongly filled cells get a red background and the value is displayed in a red color, too.
  This should clarify if a cell was revealed (grayscale), if there is an error regarding the cell (red background) or if the value itself is wrong (red value).

### Fixed

- Fixed 'Show errors' which did not work at all.
- The popup to inform that a game was won gets dismissed as soon as the user starts another game.
- Fixed deactivated number button colors in light theme which used the same colors as active ones.
- Fixed inconsistent appearance of the hint button of the main screen.

## [0.16.1] - 2023-12-25

### Fixed

- Fixed F-Droid build by ignoring a local key store if it does not exist.

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
