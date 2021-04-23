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

package plugily.projects.thebridge.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.options.ArenaOption;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class PlaceholderManager extends PlaceholderExpansion {

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String getIdentifier() {
    return "TheBridge";
  }

  @Override
  public String getAuthor() {
    return "Plugily Projects";
  }

  @Override
  public String getVersion() {
    return "1.0.1";
  }

  @Override
  public String onPlaceholderRequest(Player player, String id) {
    if(player == null) {
      return null;
    }
    switch(id.toLowerCase()) {
      case "kills":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.KILLS));
      case "deaths":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.DEATHS));
      case "games_played":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.GAMES_PLAYED));
      case "wins":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.WINS));
      case "loses":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.LOSES));
      case "points":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.SCORED_POINTS));
      case "level":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.LEVEL));
      case "exp":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.XP));
      case "exp_to_next_level":
        return String.valueOf(Math.ceil(Math.pow(50 * StatsStorage.getUserStats(player, StatsStorage.StatisticType.LEVEL), 1.5)));
      case "arena_players_online":
        return Integer.toString(ArenaRegistry.getArenaPlayersOnline());
      default:
        return handleArenaPlaceholderRequest(id);
    }
  }

  private String handleArenaPlaceholderRequest(String id) {
    if(!id.contains(":")) {
      return null;
    }
    String[] data = id.split(":");
    Arena arena = ArenaRegistry.getArena(data[0]);
    if(arena == null) {
      return null;
    }
    switch(data[1].toLowerCase()) {
      case "players":
        return String.valueOf(arena.getPlayers().size());
      case "max_players":
        return String.valueOf(arena.getMaximumPlayers());
      case "state":
        return String.valueOf(arena.getArenaState());
      case "state_pretty":
        return arena.getArenaState().getPlaceholder();
      case "mapname":
        return arena.getMapName();
      case "mode":
        return arena.getMode().toString();
      case "basesize":
        return arena.getBases().get(0).getMaximumSize().toString();
      case "modevalue":
        return String.valueOf(arena.getOption(ArenaOption.MODE_VALUE));
      case "resetblocks":
        return String.valueOf(arena.getOption(ArenaOption.RESET_BLOCKS));
      default:
        return null;
    }
  }
}
