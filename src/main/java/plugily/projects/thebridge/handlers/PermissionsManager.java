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

package plugily.projects.thebridge.handlers;

import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.utils.Debugger;

import java.util.logging.Level;

/**
 * @author Tigerpanzer, 2Wild4You
 * <p>
 * Created at 03.08.2018
 */
public class PermissionsManager {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private static String joinFullPerm = "thebridge.fullgames";
  private static String joinPerm = "thebridge.join.<arena>";

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
    PermissionsManager.setJoinFullGames(plugin.getConfig().getString("Basic-Permissions.Full-Games-Permission", "thebridge.fullgames"));
    PermissionsManager.setJoinPerm(plugin.getConfig().getString("Basic-Permissions.Join-Permission", "thebridge.join.<arena>"));
    Debugger.debug(Level.INFO, "Basic permissions registered");
  }

}
