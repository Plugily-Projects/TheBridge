package plugily.projects.thebridge.handlers.setup;

import org.bukkit.entity.Player;
import plugily.projects.thebridge.arena.Arena;

import java.util.HashMap;

public class BaseUtilities {

  private static HashMap<Player, HashMap<String, Integer>> baseId = new HashMap<>();

  public static HashMap<Player, HashMap<String, Integer>> getBaseId() {
    return baseId;
  }

  public static boolean check(Arena arena, Player player) {
    if (!BaseUtilities.getBaseId().containsKey(player)) {
      return false;
    }
    return BaseUtilities.getBaseId().get(player).containsKey(arena.getId());
  }
}
