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

package plugily.projects.thebridge.api;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.user.data.MysqlManager;
import plugily.projects.thebridge.utils.MessageUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class StatsStorage {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  private static Map sortByValue(Map<?, ?> unsortMap) {
    List list = new LinkedList<>(unsortMap.entrySet());
    list.sort((o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue()));
    Map sortedMap = new LinkedHashMap();
    for (Object sort : list) {
      Map.Entry entry = (Map.Entry) sort;
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }

  /**
   * Get all UUID's sorted ascending by Statistic Type
   *
   * @param stat Statistic type to get (kills, deaths etc.)
   * @return Map of UUID keys and Integer values sorted in ascending order of requested statistic type
   */
  @NotNull
  @Contract("null -> fail")
  public static Map<UUID, Integer> getStats(StatisticType stat) {
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      try (Connection connection = plugin.getMysqlDatabase().getConnection()) {
        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery("SELECT UUID, " + stat.getName() + " FROM " + ((MysqlManager) plugin.getUserManager().getDatabase()).getTableName() + " ORDER BY " + stat.getName());
        Map<java.util.UUID, java.lang.Integer> column = new LinkedHashMap<>();
        while (set.next()) {
          column.put(java.util.UUID.fromString(set.getString("UUID")), set.getInt(stat.getName()));
        }
        return column;
      } catch (SQLException e) {
        plugin.getLogger().log(Level.WARNING, "SQLException occurred! " + e.getSQLState() + " (" + e.getErrorCode() + ")");
        MessageUtils.errorOccurred();
        Bukkit.getConsoleSender().sendMessage("Cannot get contents from MySQL database!");
        Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
        return Collections.emptyMap();
      }
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "stats");
    Map<UUID, Integer> stats = new TreeMap<>();
    for (String string : config.getKeys(false)) {
      if (string.equals("data-version")) {
        continue;
      }
      stats.put(UUID.fromString(string), config.getInt(string + "." + stat.getName()));
    }
    return sortByValue(stats);
  }

  /**
   * Get user statistic based on StatisticType
   *
   * @param player        Online player to get data from
   * @param statisticType Statistic type to get (kills, deaths etc.)
   * @return int of statistic
   * @see StatisticType
   */
  public static int getUserStats(Player player, StatisticType statisticType) {
    return plugin.getUserManager().getUser(player).getStat(statisticType);
  }

  /**
   * Set user statistic based on StatisticType
   *
   * @param player        Online player to get data from
   * @param statisticType Statistic type to get (kills, deaths etc.)
   * @param value        int of statistic
   * @see StatisticType
   */
  public static void setUserStat(Player player, StatisticType statisticType, int value){
    plugin.getUserManager().getUser(player).setStat(statisticType, value);
  }

  /**
   * Available statistics to get.
   */
  public enum StatisticType {
    DEATHS("deaths", true), GAMES_PLAYED("gamesplayed", true), KILLS("kills", true),
    LOSES("loses", true), WINS("wins", true), LOCAL_DEATHS("local_deaths",false),
    LOCAL_KILLS("local_kills", false), LEVEL("level", true), SCORED_POINTS("points", true),
    LOCAL_SCORED_POINTS("local_points", false);

    private final String name;
    private final boolean persistent;

    StatisticType(String name, boolean persistent) {
      this.name = name;
      this.persistent = persistent;
    }

    public String getName() {
      return name;
    }

    public boolean isPersistent() {
      return persistent;
    }
  }

}
