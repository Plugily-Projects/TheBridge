package plugily.projects.amongcraft.handlers;

import plugily.projects.amongcraft.Main;
import plugily.projects.amongcraft.utils.Debugger;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 31.10.2020
 */
public class PermissionsManager {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private static String joinFullPerm = "amongcraft.fullgames";
  private static String joinPerm = "amongcraft.join.<arena>";

  public static void init() {
    setupPermissions();
  }

  public static String getJoinFullGames() {
    return joinFullPerm;
  }

  private static void setJoinFullGames(String joinFullGames) {
    PermissionsManager.joinFullPerm = joinFullGames;
  }

  public static String getJoinPerm() {
    return joinPerm;
  }

  private static void setJoinPerm(String joinPerm) {
    PermissionsManager.joinPerm = joinPerm;
  }

  private static void setupPermissions() {
    PermissionsManager.setJoinFullGames(plugin.getConfig().getString("Basic-Permissions.Full-Games-Permission", "amongcraft.fullgames"));
    PermissionsManager.setJoinPerm(plugin.getConfig().getString("Basic-Permissions.Join-Permission", "amongcraft.join.<arena>"));
    Debugger.debug(Level.INFO, "Basic permissions registered");
  }

}
