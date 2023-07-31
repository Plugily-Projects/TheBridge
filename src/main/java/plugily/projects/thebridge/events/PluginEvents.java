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

package plugily.projects.thebridge.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;

/**
 * @author Tigerpanzer_02
 *     <p>Created at 23.11.2020
 */
public class PluginEvents implements Listener {

  private final Main plugin;

  public PluginEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    if (plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if (event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if (arena == null) {
      return;
    }
    if (plugin.getConfigPreferences().getOption("FOOD_LOSE")) {
      event.setCancelled(true);
      event.setFoodLevel(20);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onAppleConsume(PlayerItemConsumeEvent event) {
    Player player = event.getPlayer();
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if (arena == null) {
      return;
    }
    if (event.getItem().getType() == XMaterial.GOLDEN_APPLE.parseMaterial()) {
      player.setFoodLevel(20);
      player.setHealth(VersionUtils.getMaxHealth(player));
      player.removePotionEffect(PotionEffectType.REGENERATION);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  // highest priority to fully protect our game
  public void onBlockBreakEvent(BlockBreakEvent event) {
    if (plugin.getArenaRegistry().isInArena(event.getPlayer())
        && plugin.getArenaRegistry().getArena(event.getPlayer()).getArenaState()
            != ArenaState.IN_GAME) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  // highest priority to fully protect our game
  public void onBuild(BlockPlaceEvent event) {
    if (plugin.getArenaRegistry().isInArena(event.getPlayer())
        && plugin.getArenaRegistry().getArena(event.getPlayer()).getArenaState()
            != ArenaState.IN_GAME) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    if (!plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      return;
    }
    if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
      event.setCancelled(true);
    }
    if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
      event.setCancelled(true);
    }
  }
}
