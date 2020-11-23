package plugily.projects.thebridge.arena;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.utils.Debugger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class ArenaRegistry {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private static final List<Arena> arenas = new ArrayList<>();
  private static int bungeeArena = -999;

  /**
   * Checks if player is in any arena
   *
   * @param player player to check
   * @return [b]true[/b] when player is in arena, [b]false[/b] if otherwise
   */
  public static boolean isInArena(Player player) {
    for (Arena arena : arenas) {
      if (arena.getPlayers().contains(player)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns arena where the player is
   *
   * @param p target player
   * @return Arena or null if not playing
   * @see #isInArena(Player) to check if player is playing
   */
  public static Arena getArena(Player p) {
    if (p == null || !p.isOnline()) {
      return null;
    }
    for (Arena arena : arenas) {
      for (Player player : arena.getPlayers()) {
        if (player.getUniqueId().equals(p.getUniqueId())) {
          return arena;
        }
      }
    }
    return null;
  }

  /**
   * Returns arena based by ID
   *
   * @param id name of arena
   * @return Arena or null if not found
   */
  public static Arena getArena(String id) {
    for (Arena loopArena : arenas) {
      if (loopArena.getId().equalsIgnoreCase(id)) {
        return loopArena;
      }
    }

    return null;
  }

  public static void registerArena(Arena arena) {
    Debugger.debug("Registering new game instance {0}", arena.getId());
    arenas.add(arena);
  }

  public static void unregisterArena(Arena arena) {
    Debugger.debug("Unegistering game instance {0}", arena.getId());
    arenas.remove(arena);
  }

  public static void registerArenas() {
    Debugger.debug("Initial arenas registration");
    long start = System.currentTimeMillis();
    if (ArenaRegistry.getArenas().size() > 0) {
      ArenaRegistry.getArenas().forEach(Arena::cleanUpArena);

      new ArrayList<>(ArenaRegistry.getArenas()).forEach(ArenaRegistry::unregisterArena);
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");

    if (!config.isConfigurationSection("instances")) {
      Debugger.sendConsoleMsg(plugin.getChatManager().colorMessage("Validator.No-Instances-Created"));
      return;
    }

    for (String id : config.getConfigurationSection("instances").getKeys(false)) {
      Arena arena;
      String s = "instances." + id + ".";
      if (s.contains("default")) {
        continue;
      }
      arena = new Arena(id);
      arena.setMinimumPlayers(config.getInt(s + "minimumplayers", 2));
      arena.setMaximumPlayers(config.getInt(s + "maximumplayers", 4));
      arena.setMapName(config.getString(s + "mapname", "none"));


      arena.setLobbyLocation(LocationSerializer.getLocation(config.getString(s + "lobbylocation", "world,364.0,63.0,-72.0,0.0,0.0")));
      arena.setEndLocation(LocationSerializer.getLocation(config.getString(s + "Endlocation", "world,364.0,63.0,-72.0,0.0,0.0")));

      if (!config.getBoolean(s + "isdone", false)) {
        Debugger.sendConsoleMsg(plugin.getChatManager().colorMessage("Validator.Invalid-Arena-Configuration").replace("%arena%", id).replace("%error%", "NOT VALIDATED"));
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);
        continue;
      }
      ArenaRegistry.registerArena(arena);
      arena.start();
      Debugger.sendConsoleMsg(plugin.getChatManager().colorMessage("Validator.Instance-Started").replace("%arena%", id));
    }
    Debugger.debug("Arenas registration completed, took {0}ms", System.currentTimeMillis() - start);
  }

  public static List<Arena> getArenas() {
    return arenas;
  }

  public static void shuffleBungeeArena() {
    bungeeArena = new Random().nextInt(arenas.size());
  }

  public static int getBungeeArena() {
    if (bungeeArena == -999) {
      bungeeArena = new Random().nextInt(arenas.size());
    }
    return bungeeArena;
  }
}