package plugily.projects.thebridge.user.data;

import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.user.User;
import plugily.projects.thebridge.utils.MessageUtils;
import plugily.projects.thebridge.utils.Debugger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajerlair.commonsbox.database.MysqlDatabase;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 31.10.2020
 */
public class MysqlManager implements UserDatabase {

  private final Main plugin;
  private final MysqlDatabase database;

  public MysqlManager(Main plugin) {
    this.plugin = plugin;
    database = plugin.getMysqlDatabase();
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      try (Connection connection = database.getConnection()) {
        Statement statement = connection.createStatement();
        // TODO
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + getTableName() + "` (\n"
          + "  `UUID` char(36) NOT NULL PRIMARY KEY,\n"
          + "  `name` varchar(32) NOT NULL,\n"
          + "  `kills` int(11) NOT NULL DEFAULT '0',\n"
          + "  `deaths` int(11) NOT NULL DEFAULT '0',\n"
          + "  `gamesplayed` int(11) NOT NULL DEFAULT '0',\n"
          + "  `wins` int(11) NOT NULL DEFAULT '0',\n"
          + "  `loses` int(11) NOT NULL DEFAULT '0'\n"
          + ");");
      } catch (SQLException e) {
        e.printStackTrace();
        MessageUtils.errorOccurred();
        Debugger.sendConsoleMsg("Cannot save contents to MySQL database!");
        Debugger.sendConsoleMsg("Check configuration of mysql.yml file or disable mysql option in config.yml");
      }
    });
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      database.executeUpdate("UPDATE " + getTableName() + " SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';");
      Debugger.debug(Level.INFO, "Executed MySQL: " + "UPDATE " + getTableName() + " SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';");
    });
  }

  @Override
  public void saveAllStatistic(User user) {
    StringBuilder update = new StringBuilder(" SET ");
    for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      if (!stat.isPersistent()) continue;
      if (update.toString().equalsIgnoreCase(" SET ")) {
        update.append(stat.getName()).append("=").append(user.getStat(stat));
      }
      update.append(", ").append(stat.getName()).append("=").append(user.getStat(stat));
    }
    String finalUpdate = update.toString();

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
      database.executeUpdate("UPDATE " + getTableName() + finalUpdate + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';"));
  }

  @Override
  public void loadStatistics(User user) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      String uuid = user.getPlayer().getUniqueId().toString();
      try (Connection connection = database.getConnection()) {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * from " + getTableName() + " WHERE UUID='" + uuid + "';");
        if (rs.next()) {
          //player already exists - get the stats
          Debugger.debug(Level.INFO, "MySQL Stats | Player {0} already exist. Getting Stats...", user.getPlayer().getName());
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
            if (!stat.isPersistent()) continue;
            int val = rs.getInt(stat.getName());
            user.setStat(stat, val);
          }
        } else {
          //player doesn't exist - make a new record
          Debugger.debug(Level.INFO, "MySQL Stats | Player {0} does not exist. Creating new one...", user.getPlayer().getName());
          statement.executeUpdate("INSERT INTO " + getTableName() + " (UUID,name) VALUES ('" + uuid + "','" + user.getPlayer().getName() + "');");
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
            if (!stat.isPersistent()) continue;
            if (stat == StatsStorage.StatisticType.CONTRIBUTION_DETECTIVE || stat == StatsStorage.StatisticType.CONTRIBUTION_MURDERER) {
              user.setStat(stat, 1);
            } else {
              user.setStat(stat, 0);
            }
          }
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

  public String getTableName() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "mysql");
    return config.getString("table", "playerstats");
  }

  public MysqlDatabase getDatabase() {
    return database;
  }

}
