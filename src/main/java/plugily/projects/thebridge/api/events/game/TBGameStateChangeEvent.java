/*
 * thebridge - Jump into the portal of your opponent and collect points to win!
 * Copyright (C) 2020  Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 */

package plugily.projects.thebridge.api.events.game;

import org.bukkit.event.HandlerList;
import plugily.projects.thebridge.api.events.thebridgeEvent;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaState;

/**
 * @author Tigerpanzer, 2Wild4You
 * @since 0.0.1-alpha
 * <p>
 * Called when arena game state has changed.
 */
public class TBGameStateChangeEvent extends thebridgeEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private final ArenaState arenaState;

  public TBGameStateChangeEvent(Arena eventArena, ArenaState arenaState) {
    super(eventArena);
    this.arenaState = arenaState;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public ArenaState getArenaState() {
    return arenaState;
  }
}
