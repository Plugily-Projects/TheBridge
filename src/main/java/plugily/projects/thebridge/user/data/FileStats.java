package plugily.projects.thebridge.user.data;

import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.user.User;

import org.bukkit.configuration.file.FileConfiguration;

import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * @author Plajer
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
