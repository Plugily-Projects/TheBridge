package plugily.projects.thebridge.user;

import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.Main;
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
 * @author Plajer
 * <p>
 * Created at 31.10.2020
 */
public class UserManager {

  private final List<User> users = new ArrayList<>();
  private final UserDatabase database;

  public UserManager(Main plugin) {
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      database = new MysqlManager(plugin);
      Debugger.debug("MySQL Stats enabled");
    } else {
      database = new FileStats(plugin);
      Debugger.debug("File Stats enabled");
    }
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
