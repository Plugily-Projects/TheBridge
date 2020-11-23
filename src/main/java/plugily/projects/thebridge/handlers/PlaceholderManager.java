package plugily.projects.thebridge.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;

/**
 * @author Tigerpanzer_02 & 2Wild4You
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
    return "thebridge";
  }

  @Override
  public String getAuthor() {
    return "Tigerpanzer_02 & 2Wild4You";
  }

  @Override
  public String getVersion() {
    return "1.0.1";
  }

  @Override
  public String onPlaceholderRequest(Player player, String id) {
    if (player == null) {
      return null;
    }
    switch (id.toLowerCase()) {
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
      default:
        return handleArenaPlaceholderRequest(id);
    }
  }

  private String handleArenaPlaceholderRequest(String id) {
    if (!id.contains(":")) {
      return null;
    }
    String[] data = id.split(":");
    Arena arena = ArenaRegistry.getArena(data[0]);
    if (arena == null) {
      return null;
    }
    switch (data[1].toLowerCase()) {
      case "players":
        return String.valueOf(arena.getPlayers().size());
      case "max_players":
        return String.valueOf(arena.getMaximumPlayers());
      case "state":
        return String.valueOf(arena.getArenaState());
      case "state_pretty":
        return arena.getArenaState().getFormattedName();
      case "mapname":
        return arena.getMapName();
      default:
        return null;
    }
  }
}
