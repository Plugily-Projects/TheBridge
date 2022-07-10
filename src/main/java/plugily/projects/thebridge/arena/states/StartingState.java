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

package plugily.projects.thebridge.arena.states;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginStartingState;
import plugily.projects.thebridge.api.events.game.TBRoundStartEvent;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.base.Base;

import java.awt.print.Paper;
import java.util.Comparator;

/**
 * @author Plajer
 * <p>Created at 03.06.2019
 */
public class StartingState extends PluginStartingState {

  @Override
  public void handleCall(PluginArena arena) {
    Arena pluginArena = (Arena) getPlugin().getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    /*
           //reset local variables to be 100% sure
           plugin.getUserManager().getUser(player).setStat(StatsStorage.StatisticType.LOCAL_DEATHS, 0);
           plugin.getUserManager().getUser(player).setStat(StatsStorage.StatisticType.LOCAL_KILLS, 0);
           plugin.getUserManager().getUser(player).setStat(StatsStorage.StatisticType.LOCAL_SCORED_POINTS, 0);
           //
    */
    boolean arenaStart = false;
    if(arena.getTimer() == 0 || arena.isForceStart()) {
      arenaStart = true;
      for(Player player : arena.getPlayers()) {
        // get base with min players
        Base minPlayers =
            pluginArena.getBases().stream().min(Comparator.comparing(Base::getPlayersSize)).get();
        // add player to min base if he got no base
        if(!pluginArena.inBase(player)) {
          minPlayers.addPlayer(player);
        }
        // fallback
        if(!pluginArena.inBase(player)) {
          pluginArena.getBases().get(0).addPlayer(player);
        }
        getPlugin().getUserManager().addExperience(player, 10);
      }
      // check if not only one base got players
      Base maxPlayers =
          pluginArena.getBases().stream().max(Comparator.comparing(Base::getPlayersSize)).get();
      Base minPlayers =
          pluginArena.getBases().stream().min(Comparator.comparing(Base::getPlayersSize)).get();
      if(maxPlayers.getPlayersSize() == pluginArena.getPlayers().size()) {
        for(int i = 0; i < maxPlayers.getPlayersSize() / 2; i++) {
          Player move = maxPlayers.getPlayers().get(i);
          minPlayers.addPlayer(move);
          maxPlayers.removePlayer(move);
        }
      }
      Bukkit.getPluginManager().callEvent(new TBRoundStartEvent(pluginArena));
      for(Base base : pluginArena.getBases()) {
        base.removeCageFloor();
      }
    }
    super.handleCall(arena);
    if(arenaStart) {
      //needs to be executed after handle call as start location does not exists on thebridge
      pluginArena.teleportAllToBaseLocation();
    }
  }
}
