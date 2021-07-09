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

package plugily.projects.thebridge.arena;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.utils.Utils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 23.11.2020
 */
public class ArenaUtils {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  public static int emptyBases(Arena arena) {
    return (int) arena.getBases().stream()
        .filter(base -> base.getPlayersSize() == 0)
        .count();
  }

  public static boolean areInSameArena(Player one, Player two) {
    return ArenaRegistry.getArena(one) != null && ArenaRegistry.getArena(one).equals(ArenaRegistry.getArena(two));
  }

  public static void hidePlayer(Player p, Arena arena) {
    for(Player player : arena.getPlayers()) {
      VersionUtils.hidePlayer(plugin, player, p);
    }
  }

  public static void showPlayer(Player p, Arena arena) {
    for(Player player : arena.getPlayers()) {
      VersionUtils.showPlayer(plugin, player, p);
    }
  }

  public static void resetPlayerAfterGame(Player player) {
    for(Player players : plugin.getServer().getOnlinePlayers()) {
      VersionUtils.showPlayer(plugin, player, players);
      if(!ArenaRegistry.isInArena(players)) {
        VersionUtils.showPlayer(plugin, players, player);
      }
    }
    player.setGameMode(GameMode.SURVIVAL);
    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    player.setFlying(false);
    player.setAllowFlight(false);
    player.getInventory().clear();
    player.getInventory().setArmorContents(null);
    VersionUtils.setMaxHealth(player, 20.0);
    player.setHealth(VersionUtils.getHealth(player));
    player.setFireTicks(0);
    player.setFoodLevel(20);
    player.setWalkSpeed(0.2f);
    player.setLevel(0);
    player.setExp(0);
    VersionUtils.setCollidable(player, true);
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.loadInventory(plugin, player);
    }
  }

  public static void hidePlayersOutsideTheGame(Player player, Arena arena) {
    for(Player players : plugin.getServer().getOnlinePlayers()) {
      if(arena.getPlayers().contains(players)) {
        continue;
      }
      VersionUtils.hidePlayer(plugin, player, players);
      VersionUtils.hidePlayer(plugin, players, player);
    }
  }

  public static void arenaForceStart(Player player) {
    if(!Utils.hasPermission(player, "thebridge.admin.forcestart")) {
      return;
    }
    if(!Utils.checkIsInGameInstance(player)) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(player);
    if(arena.getPlayers().size() < 2) {
      plugin.getChatManager().broadcast(arena, plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players"), arena.getMinimumPlayers()));
      return;
    }
    if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
      arena.setArenaState(ArenaState.STARTING);
      arena.setForceStart(true);
      arena.setTimer(0);
      for(Player players : ArenaRegistry.getArena(player).getPlayers()) {
        players.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"));
      }
    }
  }

}
