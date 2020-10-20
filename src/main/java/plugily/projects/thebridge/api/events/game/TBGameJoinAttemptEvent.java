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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import plugily.projects.thebridge.api.events.thebridgeEvent;
import plugily.projects.thebridge.arena.Arena;

/**
 * @author Tigerpanzer, 2Wild4You
 * @since 0.0.1-alpha
 * <p>
 * Called when player is attempting to join arena.
 */
public class TBGameJoinAttemptEvent extends thebridgeEvent implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Player player;
  private boolean isCancelled;

  public TBGameJoinAttemptEvent(Player player, Arena targetArena) {
    super(targetArena);
    this.player = player;
    this.isCancelled = false;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public boolean isCancelled() {
    return this.isCancelled;
  }

  @Override
  public void setCancelled(boolean isCancelled) {
    this.isCancelled = isCancelled;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

}
