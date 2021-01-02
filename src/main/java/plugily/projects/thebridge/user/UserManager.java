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

package plugily.projects.thebridge.user;

import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.handlers.PermissionsManager;
import plugily.projects.thebridge.user.data.FileStats;
import plugily.projects.thebridge.user.data.MysqlManager;
import plugily.projects.thebridge.user.data.UserDatabase;
import plugily.projects.thebridge.utils.Debugger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class UserManager {

  private final List<User> users = new ArrayList<>();
  private final UserDatabase database;
  private final Main plugin;

  public UserManager(Main plugin) {
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      database = new MysqlManager(plugin);
      Debugger.debug("MySQL Stats enabled");
    } else {
      database = new FileStats(plugin);
      Debugger.debug("File Stats enabled");
    }
    this.plugin = plugin;
    loadStatsForPlayersOnline();
  }

  private void loadStatsForPlayersOnline() {
    Bukkit.getServer().getOnlinePlayers().stream().map(this::getUser).forEach(this::loadStatistics);
  }

  public User getUser(Player player) {
    for (User user : users) {
      if (user.getPlayer().equals(player)) {
        return user;
      }
    }
    Debugger.debug("Registering new user {0} ({1})", player.getUniqueId(), player.getName());
    User user = new User(player);
    users.add(user);
    return user;
  }

  public List<User> getUsers(Arena arena) {
    return arena.getPlayers().stream().map(this::getUser).collect(Collectors.toList());
  }

  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    if (!stat.isPersistent()) {
      return;
    }
    database.saveStatistic(user, stat);
  }

  public void addExperience(Player player, int i) {
    User user = plugin.getUserManager().getUser(player);
    user.addStat(StatsStorage.StatisticType.XP, i);
    if (player.hasPermission(PermissionsManager.getAllKitsPerm())) {
      user.addStat(StatsStorage.StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    updateLevelStat(player, ArenaRegistry.getArena(player));
  }

  public void addStat(Player player, StatsStorage.StatisticType stat) {
    User user = plugin.getUserManager().getUser(player);
    user.addStat(stat, 1);
    updateLevelStat(player, ArenaRegistry.getArena(player));
  }

  public void updateLevelStat(Player player, Arena arena) {
    User user = plugin.getUserManager().getUser(player);
    if (Math.pow(50.0 * user.getStat(StatsStorage.StatisticType.LEVEL), 1.5) < user.getStat(StatsStorage.StatisticType.XP)) {
      user.addStat(StatsStorage.StatisticType.LEVEL, 1);
      //Arena can be null when player has left the arena before this message the arena is retrieved.
      if(arena != null)
        player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("In-Game.You-Leveled-Up"), user.getStat(StatsStorage.StatisticType.LEVEL)));
    }
  }

  public void loadStatistics(User user) {
    database.loadStatistics(user);
  }

  public void saveAllStatistic(User user) {
    database.saveAllStatistic(user);
  }

  public void removeUser(User user) {
    users.remove(user);
  }

  public UserDatabase getDatabase() {
    return database;
  }

}
