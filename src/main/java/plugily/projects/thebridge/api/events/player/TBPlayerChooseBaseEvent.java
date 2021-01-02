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

package plugily.projects.thebridge.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import plugily.projects.thebridge.api.events.TheBridgeEvent;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.base.Base;

/**
 * @author TomTheDeveloper, Plajer
 * @since 2.0.0
 * <p>
 * Called when player chose kit in game.
 */
public class TBPlayerChooseBaseEvent extends TheBridgeEvent implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private final Base base;
  private boolean isCancelled;

  public TBPlayerChooseBaseEvent(Player player, Base base, Arena arena) {
    super(arena);
    this.player = player;
    this.base = base;
    this.isCancelled = false;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }

  public Base getBase() {
    return base;
  }

  @Override
  public boolean isCancelled() {
    return this.isCancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.isCancelled = cancelled;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
