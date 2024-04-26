# Calculating difficulties

For a game variant to get a difficulty rating, the following things happen:
* 1,000 games get created with a unique solution each.
* For each of these games, the difficulty gets calculated.
* The 1,000 difficulties get sorted and the difficulty ratings, 0 being the most easy one and 999 the most difficult one.
* The ratings are applied as follows:
  * 1/3 of the games are considered 'easy', 1/3 'medium' and 1/3 'hard'
  * the upper and lower 5% are considered 'very easy' and 'very hard'

This leads to the following ratings:

| Difficulty rating | Contained games | Number of games | Percent of games |
|-------------------|----------------:|----------------:|-----------------:|
| very easy         |            0-49 |              50 |               5% |
| easy              |          50-332 |             283 |            28,3% |
| medium            |         333-666 |             334 |            33,4% |
| hard              |         667-949 |             283 |            28,3% |
| very hard         |         950-999 |              50 |               5% |

## Current state of calculation

There are 168 variants per grid size.
Only square grids contain difficulty ratings yet.

| Grid size | Calculated variants |
|----------:|--------------------:|
|       3x3 |      100% (168/168) |
|       4x4 |      100% (168/168) |
|       5x5 |      100% (168/168) |
|       6x6 |      100% (168/168) |
|       7x7 |      100% (168/168) |
|       8x8 |      100% (168/168) |
|       9x9 |       94% (158/168) |
|     10x10 |       81% (136/168) |
|     11x11 |       67% (113/168) |

## Setup

### Local setup

The lesser complex game variants where calculated on a local mobile workstation (Lenovo P1 Gen. 2 from 2019, Intel Core i7 gen 9 'Coffee Lake', 6 cores, 12 threads, 2,6 GHz and 64 GB ram).

### Azure Sponsorship

Gauguin got the chance to participate at a Azure Sponsorship from the Microsoft Open Source Programs Office.
This is where the more complex game variants were and are calculated.

All square game variants up to 7x7 were calculated on a maschine with 70 exclusive cores.
All square variants from 8x8 to 11x11 were probed to see how much of 10 games could be solved in 5 Minutes of one thread.
This resulted in a map of each of the variants to a number from 0 to 10
* 0 meaning no games could be solved in 5 minutes and
* 10 meaning all 10 games could be solved in 5 minutes.

In the next step, all variants whose probe solved all 10 games were calculated in one run.
This step took 1,433 minutes, that's roughly one day.
As the maschine utilizes 120 cores, this would be ~120 days on a single thread maschine.

| Number of solved games out of 10 | Number of game variants | Duration in minutes, 120 cores | Duration of single thread | Duration per variant | Duration per game |
|----------------------------------|-------------------------|--------------------------------|---------------------------|----------------------|-------------------|
| 10                               | 513                     | 1,433m13s ~= 1 day             | ~120 days                 | ~168 s               | ~168 ms           |
| 8-9 *                            | 30                      | 3,231m14s ~= 2.2 days          | ~269 days                 | ~1.8 h               | ~6.5 s            |
| 6-7 *                            | 14                      | 8,929m23s ~= 2.5 days          | ~298 days                 | ~4.3 h               | ~15,3 s           |

*: Both 8-9 and 6-7 were started in parallel, so the duration has been interpolated from the absolut values.