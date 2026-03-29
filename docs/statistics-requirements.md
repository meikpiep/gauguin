# Features and requirements of statistics

The new implementation should remember all games played and calculate the relevant numbers for
statistics and best time on demand.

This should enable to add more statistics in the future using the then existing history.
This will work if the new statistics do not need new data, but use the given structure to obtain
them.

The new statistics will live next to the existing one, to avoid every user loosing their history.
The idea of the migration is

1. Gather both statistics in parallel to avoid abruptly loose the statistics
2. Optimize new statistics and gather more history
3. Delete the old statistics after a relevant time

## Ideas from the past

* Avoid showing only the last 50 games via diagrams.
* Let the user choose which grid variants to show (e.g. "all", "9x9",...)
* Tapping on a diagram will show it full screen.
* Keep Best Times for each grid variant (if feasible)
* Display "total time spend"
* Which games were solved with help and without?

## Feature list

* Filter by
  * grid size
  * other game variant (e.g. only multiplication, including zero etc.)
  * time, e.g. "games last week"
* "Comparable grids"
  * should compare only similar grid constellations so that the user gets data related to similar
    grids
  * Can a camparison done by comparing the difficulty rating of the grid variant?

## Remaining problems

## Best Time

Currently, the best time is stored by the size of the grid, e.g. 6x6.

* Different variants or difficulty levels may require their own best time.
* The best time should be understandable.

Its unclear which way to go.
