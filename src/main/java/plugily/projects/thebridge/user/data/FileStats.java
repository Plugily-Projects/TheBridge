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

package plugily.projects.thebridge.user.data;

import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.user.User;

import org.bukkit.configuration.file.FileConfiguration;

import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class FileStats implements UserDatabase {

  private final Main plugin;
  private final FileConfiguration config;

  public FileStats(Main plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, "stats");
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    config.set(user.getPlayer().getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    ConfigUtils.saveConfig(plugin, config, "stats");
  }

  @Override
  public void saveAllStatistic(User user) {
    for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      if (!stat.isPersistent()) continue;
      config.set(user.getPlayer().getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    }
    ConfigUtils.saveConfig(plugin, config, "stats");
  }

  @Override
  public void loadStatistics(User user) {
    for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      user.setStat(stat, config.getInt(user.getPlayer().getUniqueId().toString() + "." + stat.getName(), 0));
    }
  }

}
