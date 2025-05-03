/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.thebridge.arena;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaManager;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.base.Base;

import java.util.Comparator;

/**
 * @author Plajer
 * <p>Created at 13.05.2018
 */
public class ArenaManager extends PluginArenaManager {

  private final Main plugin;

  public ArenaManager(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @Override
  public void additionalPartyJoin(Player player, IPluginArena arena, Player partyLeader) {
    Arena pluginArena = plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    Base partyBase = null;
    if(arena.getPlayersLeft().contains(partyLeader)) {
      if(pluginArena.inBase(partyLeader)) {
        partyBase = pluginArena.getBase(partyLeader);
      }
    }
    if(partyBase != null) {
      partyBase.addPlayer(player);
    }
  }

  @Override
  public void leaveAttempt(@NotNull Player player, @NotNull IPluginArena arena) {
    Arena pluginArena = plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    Base base = pluginArena.getBase(player);
    if(base != null){
      base.removePlayer(player);
    }
    super.leaveAttempt(player, arena);
    if(pluginArena.isDeathPlayer(player)) {
      pluginArena.removeDeathPlayer(player);
    }
    if(arena.getArenaState() != IArenaState.WAITING_FOR_PLAYERS
      && arena.getArenaState() != IArenaState.STARTING && arena.getArenaState() != IArenaState.FULL_GAME
      && (arena.getPlayersLeft().size() <= 1
      || (arena.getPlayersLeft().size() <= arena.getArenaOption("BASE_PLAYER_SIZE")
      && pluginArena.getBases().stream()
      .max(Comparator.comparing(Base::getPlayersSize))
      .get()
      .getAlivePlayersSize()
      == arena.getPlayersLeft().size()))) {
      stopGame(true, arena);
    }
  }

  @Override
  public void stopGame(boolean quickStop, @NotNull IPluginArena arena) {
    Arena pluginArena = plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    for(Player player : arena.getPlayersLeft()) {
      if(!quickStop) {
        switch(pluginArena.getMode()) {
          default:
            break;
          case HEARTS:
            if(pluginArena.isDeathPlayer(player)) {
              plugin
                .getUserManager()
                .addStat(player, plugin.getStatsStorage().getStatisticType("LOSES"));
              plugin
                .getRewardsHandler()
                .performReward(player, arena, plugin.getRewardsHandler().getRewardType("LOSE"));
            } else {
              plugin
                .getUserManager()
                .addStat(player, plugin.getStatsStorage().getStatisticType("WINS"));
              plugin
                .getRewardsHandler()
                .performReward(player, arena, plugin.getRewardsHandler().getRewardType("WIN"));
              plugin.getUserManager().addExperience(player, 5);
            }
            break;
          case POINTS:
            if(pluginArena.getWinner().getPlayers().contains(player)) {
              plugin
                .getUserManager()
                .addStat(player, plugin.getStatsStorage().getStatisticType("WINS"));
              plugin.getUserManager().addExperience(player, 5);
              plugin
                .getRewardsHandler()
                .performReward(player, arena, plugin.getRewardsHandler().getRewardType("WIN"));
            } else {
              plugin
                .getUserManager()
                .addStat(player, plugin.getStatsStorage().getStatisticType("LOSES"));
              plugin
                .getRewardsHandler()
                .performReward(player, arena, plugin.getRewardsHandler().getRewardType("LOSE"));
            }
            break;
        }
      }
    }
    super.stopGame(quickStop, arena);
  }
}
