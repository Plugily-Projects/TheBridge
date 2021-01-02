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

package plugily.projects.thebridge.api.events.game;

import org.bukkit.event.HandlerList;
import plugily.projects.thebridge.api.events.TheBridgeEvent;
import plugily.projects.thebridge.arena.Arena;

/**
 * @author Tigerpanzer_02
 * @since 1.0.0
 * <p>
 * Called when arena is stopped.
 */
public class TBGameStopEvent extends TheBridgeEvent {

  private static final HandlerList HANDLERS = new HandlerList();

  public TBGameStopEvent(Arena arena) {
    super(arena);
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

}
