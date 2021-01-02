/*
 * TheBridge - Defend your base and try to wipe out the others
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package plugily.projects.thebridge.arena.options;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 07.11.2020
 */
public enum ArenaOption {
  /**
   * Current arena timer, ex. 30 seconds before game starts.
   */
  TIMER(0),
  /**
   * How many bases are on that arena?
   */
  BASES(2),
  /**
   * How many players per base?
   */
  SIZE(4),
  /**
   * How many points does an base need to win?
   */
  MODE_VALUE(5),
  /**
   * Reset blocks after round x
   */
  RESET_BLOCKS(0),
  /**
   * Reset blocks after round x
   */
  RESET_TIME(5),
  /**
   * Minimum players in arena needed to start.
   */
  MINIMUM_PLAYERS(2),
  /**
   * Maximum players arena can hold, users with full games permission can bypass this!
   */
  MAXIMUM_PLAYERS(8);

  private final int defaultValue;

  ArenaOption(int defaultValue) {
    this.defaultValue = defaultValue;
  }

  public int getDefaultValue() {
    return defaultValue;
  }
}
