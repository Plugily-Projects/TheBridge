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

/*
 * The Bridge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Bridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The Bridge.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * The Bridge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Bridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The Bridge.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.thebridge.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.api.events.TheBridgeEvent;
import plugily.projects.thebridge.arena.Arena;

/**
 * @author Tigerpanzer_02
 * @see StatsStorage.StatisticType
 * @since 1.0.0
 * <p>
 * Called when player receive new statistic.
 */
public class TBPlayerStatisticChangeEvent extends TheBridgeEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Player player;
  private final StatsStorage.StatisticType statisticType;
  private final int number;

  public TBPlayerStatisticChangeEvent(Arena eventArena, Player player, StatsStorage.StatisticType statisticType, int number) {
    super(eventArena);
    this.player = player;
    this.statisticType = statisticType;
    this.number = number;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public Player getPlayer() {
    return player;
  }

  public StatsStorage.StatisticType getStatisticType() {
    return statisticType;
  }

  public int getNumber() {
    return number;
  }

}
