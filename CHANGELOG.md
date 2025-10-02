# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Adds a confirmation dialog if a user requests help by e.g. revealing cells. This should avoid an
 unwanted reset of the current streak. The dialog only gets shown if the action would actually break
 a streak.

### Changed

### Deprecated

### Removed

### Fixed

- Fixes statistics screen for >999 games played: Avoids unwanted line breaks by using auto size
 texts. This optimizes screen layout for small devices like old phones.

### Security

## [0.44.0] - 2025-08-31

### Changed

- Fixes calculation of streaks in statistics.
- Enhances the readability of the math displayed in each grid cage.

### Fixed

- The screen is kept on only if the current grid has not already been solved.

## [0.43.2] - 2025-07-27

### Fixed

- Fixes dynamic colors in combination of plain black as backgrounds: The value of a grid cell was
  not drawn.

## [0.43.1] - 2025-07-17

### Changed

- If the math challenge of a cage overlaps a cells value, the challenge like '5+' gets drawn
  above the cell value to increase its readability.

### Fixed

- Fixes dynamic colors and monochrome mode not supporting plain black as backgrounds.

## [0.43.0] - 2025-07-05

### Added

- Adds new preference to directly dispose the confetti effect when starting a new game. Lets you see
  the new grid without distraction from the first second on.
- Adds link to new homepage under https://gauguin.app.
- Supports Android 16 (SDK 36).

### Changed

- Optimizes performance by saving the possible next grid to file system.
- Optimizes performance of upcoming new difficulty algorithm.
- Enhances upcoming new difficulty algorithm to solve more games.

### Fixed

- Fixes rare occurrences of 'kotlin.PropertyUninitializedPropertyAccessException'.
- Loading an already solved game no longer gets handled as its to be solved (running timer, enabling
  undo etc.)

## [0.42.0] - 2025-06-14

### Added

- Adds new 'monochrome theme', featuring a strict monochromatic main screen. This is not 100%
  polished, feel free to report problems or suggestions.

### Changed

- Optimize layout of main screen for tablets.

## [0.41.0] - 2025-05-17

### Added

- Adds support for an OLED friendly plain black background of remaining screens. This contains only
  the change of the background to plain black. Further modifications to support OLED displays may
  come in the future.
- Highlights duplicates contained in possible numbers if the cells column or row already contains
  a cell with the value put into it.

### Changed

- Changes save game format. (Uses null-able field to store the user value of a cell.)
- Small theming enhancements like using dynamic color for the hint button.
- Reword preference 'Pencil marks in 3x3 grid' to 'Layout of pencil marks' to clarify its purpose.

### Fixed

- Fixes missing menu if the theme gets changed by the using via theme chooser.

## [0.40.0] - 2025-04-27

### Added

- Adds possibility to select multiple difficulty levels when creating a new grid.
- Adds support for an OLED friendly plain black background of the main screen (first iteration).
- Grid difficulties get persisted, avoids recalculation of difficulties when loading a grid.

### Changed

- Introduces versioning of the save game format with on-demand migration to the latest version.
  Caused by supporting multiple difficulty levels at once.

### Fixed

- Activate workaround with the new edge-to-edge layout on Android 10.
- Fix layout issues regarding edge-to-edge layout, mostly dealing with tablets.

## [0.39.1] - 2025-04-16

### Fixed

- Avoid consuming key presses while playing when the key press is not related to Gauguin. Fixes
  devices not reacting on pressing the volume keys.

## [0.39.0] - 2025-04-14

### Added

- Adds translation into Russian.
- Lets you fill a cell of a single cage by long pressing onto the cell.
- Supports Android 15 via target SDK version.
- Implements edge to edge support. Please report any issues caused by this, as there are many
  dimensions and device types out in the wild.

## [0.38.0] - 2025-04-09

### Added

- Make the grid playable via physical keyboards:
  - '<Digit>' (0..9): Add or remove a possible value
  - '<Meta>' + '<Digit>' or '<Shift>' + '<Digit>' Fill in a value directly via 
  - '<Space>' or '<Enter>': Fill single possible via 
  - '<Del>': Delete all possibles of selected cell
  - '<Backspace>': Undo last step
  - '<?>' show hint popup

## [0.37.2] - 2025-04-08

### Fixed

- Fixes inconsistent release preparation.

## [0.37.1] - 2025-04-08

### Fixed

- Fixes inconsistent release preparation.

## [0.37.0] - 2025-04-08

### Added

- Statistics: Highlight last game in scatter plot and streaks diagram.

### Changed

- Statistics: Compress streak diagram to show one streak per column. Avoids redundant entries when
  showing long streaks.

## [0.36.0] - 2025-03-30

### Changed

- Optimize for split screen on phones.

### Fixed

- Avoid sliding in the menu into the main screen by dragging from one side of the screen. This
  ensures that while interacting with the grid, the the menu does not start to slide in from the
  side. To open the menu, use the menu button located at the corner below.

## [0.35.0] - 2025-03-17

### Changed

- Speeds up new difficulty algorithm (only available in debug variant).
- Speeds up creation of preview grid.

### Fixed

- Disable undo functionality if the current grid has already been solved.
- Avoid change of focus when revealing the cells of the current cage.

## [0.34.1] - 2025-02-03

### Added

- More info if an exception occurs while loading grids.

### Changed

- Downgrade JSON dependency to hopefully fix exceptions when loading grids.

## [0.34.0] - 2025-01-26

### Added

- Adds separate choosing of the theme and night mode to use. Enables using dynamic colors in dark
  mode or using the system setting of the light/dark mode.
- Debug version only: Adds new difficulty classification algorithm. It needs balancing and
  performance optimizations.

### Fixed

- Fixed a bug crashing the app if the settings dialog was opened and the device does not support
  dynamic colors.
- Avoid setting dynamic color mode if the device is not capable of this feature.

## [0.33.0] - 2024-12-21

### Changed

- Moved UI for theme switching from preferences screen to the main menu. Just open the burger menu
  and find the button straight on top.

### Removed

- Remove the `DependencyInfoBlock` from Gauguin APK. Avoids Google cipher magic.

### Fixed

- Fix theme switching if either the old or the new theme is the dynamic theme from Material You.

## [0.32.0] - 2024-11-10

### Added

- The undo steps a a grid are included when it gets saved. This means that the undo steps get lost
  no longer it the grid gets loaded, including auto-saved grids.

### Changed

- Show the game solved dialog if the grid shown at the start of the app has already been solved.
- Build against Android 15 (that is SDK 35). Proper edge to edge support has still to come.

## [0.31.3] - 2024-10-19

### Fixed

- Main screen did not update when starting a new game in some circumstances.
- Fix missing average marker in statistics.

## [0.31.2] - 2024-10-13

### Fixed

- Fix crash during start if the app has never been started before.

## [0.31.1] - 2024-10-10

### Fixed

- Fix crash using Android 8 and 9 by not reusing font builder instances.

## [0.31.0] - 2024-09-30

### Changed

- Change font to display numbers in grid from Lato to Inter.
- Upgrade Kotlin from 1.9.x to 2.0.20, including dependencies.

### Fixed

- Fix Ferris Wheel animation by re-adding Ferris Wheel lib using our own fork, see
  [https://github.com/meikpiep/Ferris-Wheel](https://github.com/meikpiep/Ferris-Wheel).
- Fix too high similarity between mathematical symbol '×' for multiplication and '+' for addition by
 switching the font from Lato to Inter.

## [0.30.3] - 2024-09-16

### Fixed

- Temporary remove any usage of the Ferris Wheel lib. This means no animation when generating a new
  grid. The lib was only deployed via JCenter which was shutdown recently, lacking any repository
  to download the lib for a build.
  We will work to get the lib/animation back soon.

## [0.30.2] - 2024-09-16

### Fixed

- Fix F-Droid build by using a local copy of the Ferris Wheel lib contained in the code repository.
  Caused by a overseen hard shutdown of the deprecated JCenter servers. This is a workaround,
  the libs last update was 2018, we will see if any new artifacts can be published in a convenient
  way.

## [0.30.1] - 2024-09-14

### Fixed

- Adds missing changelog entries in metadata.

## [0.30.0] - 2024-09-14

### Added

- Scatter plot visualizing the difficulty and duration of the grids played.

### Changed

- Complete overhaul of the statistics UI, using bento grids. Further improvements of gathering
 statistics are likely to come.

## [0.29.1] - 2024-08-07

### Changed

- Cells with a revealed value are immutable.

### Fixed

- Fixed bug where one could reveal cells even if the current game was already solved.
- Avoid overlapping bottom app bar items if the main screen is in landscape mode.

## [0.29.0] - 2024-07-22

### Added

- Adds changelog info via F-Droid by using Fastlane metadata format.

### Changed

- Use Fastlane instead of Triple-T Gradle Plugin to upload releases to Play Store.

### Fixed

- Fix layout of new game screen for small devices in landscape mode.

## [0.28.0] - 2024-07-07

### Added

- Translation into Traditional Chinese.

### Fixed

- Fix missing grid preview in new game screen, which occurred for some popular screen types.
- Fix generating rectangular grids.
- Fix equalizing best time to no longer be handled as a new best time.

## [0.27.0] - 2024-06-12

### Added

- Add possibility to share application log via button at about dialog.

## [0.26.0] - 2024-06-12

### Changed

- Pencil marks using with 3x3 preference have a layout consistent to the number key pad layout.
- Optimize layout of pencils marks via 3x3 preference if using a maximum of 6 values.

### Removed

- Remove code deleting legacy save games at start up. Old save games were migrated to the new format
 from quite few versions on. Recent versions did not contain the migration code anymore and just
 deleted the old games. From know on, these old games get ignored at all.
 If you ever did install a non-ancient version of Gauguin, your games will already have been
 migrated.

### Fixed

- Avoid rare exception on game initialization.
- Fix main screen being stuck if a new game has not been calculated yet.

## [0.25.0] - 2024-05-10

### Added

- Translation into Arabic.
- Adds difficulty ratings of most variants of square grid sizes from 8x8 to 11x11. Includes a slight
  change of existing difficulty ratings. For background information about the difficulty ratings see
  [separate documentation](docs/calculating-difficulties.md).

### Changed

- Replaces the about dialog with a more modern one, using a regular activity to cover the whole
 screen.
- Updates Android Material to version 1.12 (Beware of the slider!).

### Fixed

- Try to fix the bug where the hint popup shows an obviously wrong number of mistakes, once again.

## [0.24.2] - 2024-04-13

### Fixed

- Fixes failing unit tests which were overseen. Affects F-Droid build only which would fail if a
 test fails. Play Store and the APK at GitHub are not affected as they do not relate to test code.

## [0.24.1] - 2024-04-11

### Fixed

- Try to fix the bug where the hint popup shows an obviously wrong number of mistakes.
  This is done by cleaning up the code regarding to the state of the game in the hope this may
  solve the issue.

## [0.24.0] - 2024-04-03

### Added

- Translation into French.
- Add language choosing per system settings from Android 13 on.

## [0.23.0] - 2024-03-19

### Added

- Translation into Simplified Chinese.
- Switch to compact key pad buttons and alter the layout automatically if a grid with size greater
  than 9 gets played and the display dimensions are phone wise limited.
- New option the allow grids with size greater than 9 to obtain most of the screen, which means
  getting a rectangular (that is a non-square) grid layout. This may look a bit less elegant than
  the usual one, but features more space to display readable pencil marks and so on.

### Fixed

- The info box shown when finishing a game gets no longer squeezed if there is not enough space left
  for it. Instead of, the box may overlap the grid.
  This is not pretty, but a kind of workaround especially when using a rectangular grid shape,
  minimizing the space left to be used by e.g. this info box.

## [0.22.1] - 2024-03-11

### Fixed

- Fixed popup showing difficulty ratings in colors missing necessary contrast to be readable.
- Fixed hint popup not showing when using landscape orientation on a phone.

## [0.22.0] - 2024-03-10

### Added

- Added all difficulty levels of 7x7 and 8x8 grids.

### Removed

- Removed migration of old save file format. The new format has been introduced in version 0.18.0
  in January 2024. Every version from 0.18.0 until including 0.21.1 migrates all saved games at app
  start automatically.
  If you update from <= 0.18.0 to >= 0.22.0, all games will be deleted.

### Fixed

- Fixed restarting a solved game.
- Fixed display of the chosen numeral system in new game screen.
- Fixed minor layout issues.

## [0.21.1] - 2024-03-01

### Fixed

- Fixed the hint popup blocking the following tap in the way that the game seems to not respond.

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

* Remove the `DependencyInfoBlock` from Gauguin APK. Avoids Google cipher magic.

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
