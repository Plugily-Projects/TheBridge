package plugily.projects.thebridge.boot;

import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.api.StatsStorage;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOptionManager;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItemManager;
import plugily.projects.minigamesbox.classic.handlers.permissions.Permission;
import plugily.projects.minigamesbox.classic.handlers.permissions.PermissionsManager;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardsFactory;
import plugily.projects.minigamesbox.classic.preferences.ConfigOption;
import plugily.projects.minigamesbox.classic.preferences.ConfigPreferences;
import plugily.projects.thebridge.Main;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class AdditionalValueInitializer {

  private final Main plugin;

  public AdditionalValueInitializer(Main plugin) {
    this.plugin = plugin;
    registerConfigOptions();
    registerStatistics();
    registerPermission();
    registerRewards();
    registerSpecialItems();
    registerArenaOptions();
  }

  private void registerConfigOptions() {
    getConfigPreferences().registerOption("NATURAL_REGENERATION", new ConfigOption("Natural-Regeneration", false));
    getConfigPreferences().registerOption("CAGE_ONLY_FLOOR", new ConfigOption("Cage.Only-Floor", false));
    getConfigPreferences().registerOption("BLOCK_BREAK_DROP", new ConfigOption("Block-Break-Drop", true));
  }

  private void registerStatistics() {
    getStatsStorage().registerStatistic("KILLS", new StatisticType("kills", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("DEATHS", new StatisticType("deaths", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("SCORED_POINTS", new StatisticType("scored_points", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_SCORED_POINTS", new StatisticType("local_scored_points", false, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_KILLS", new StatisticType("local_kills", false, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_POINTS", new StatisticType("local_points", false, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_DEATHS", new StatisticType("local_deaths", false, "int(11) NOT NULL DEFAULT '0'"));
  }

  private void registerPermission() {
    //    getPermissionsManager().registerPermissionCategory("CHANCES_BOOSTER", new PermissionCategory("Chances-Boost", null));
    getPermissionsManager().registerPermission("KIT_PREMIUM_UNLOCK", new Permission("Basic.Premium-Kits", "thebridge.kits.premium"));
  }

  private void registerRewards() {
    getRewardsHandler().registerRewardType("WIN", new RewardType("win"));
    getRewardsHandler().registerRewardType("LOSE", new RewardType("lose"));
    getRewardsHandler().registerRewardType("PLAYER_DEATH", new RewardType("player-death"));
    getRewardsHandler().registerRewardType("KILL", new RewardType("kill"));
    getRewardsHandler().registerRewardType("DEATH", new RewardType("death"));
    getRewardsHandler().registerRewardType("POINT", new RewardType("point"));
    getRewardsHandler().registerRewardType("RESET_ROUND", new RewardType("reset-round"));
  }

  private void registerSpecialItems() {
    getSpecialItemManager().registerSpecialItem("BASE_SELECTOR", "Base-Selector");
    getSpecialItemManager().registerSpecialItem("KIT_SELECTOR_MENU", "Kit-Menu");
  }

  private void registerArenaOptions() {
    /**
     * How many bases are on that arena?
     */
    getArenaOptionManager().registerArenaOption("BASE_AMOUNT", new ArenaOption("null", 2));
    /**
     * How many players per base?
     */
    getArenaOptionManager().registerArenaOption("BASE_PLAYER_SIZE", new ArenaOption("null", 4));
    /**
     * How many points does an base need to win?
     */
    getArenaOptionManager().registerArenaOption("MODE_VALUE", new ArenaOption("null", 5));
    /**
     * Reset blocks after round x
     */
    getArenaOptionManager().registerArenaOption("RESET_BLOCKS", new ArenaOption("null", 0));
    /**
     * Reset blocks after round x
     */
    getArenaOptionManager().registerArenaOption("RESET_TIME", new ArenaOption("null", 5));
  }

  private ConfigPreferences getConfigPreferences() {
    return plugin.getConfigPreferences();
  }

  private StatsStorage getStatsStorage() {
    return plugin.getStatsStorage();
  }

  private PermissionsManager getPermissionsManager() {
    return plugin.getPermissionsManager();
  }

  private RewardsFactory getRewardsHandler() {
    return plugin.getRewardsHandler();
  }

  private SpecialItemManager getSpecialItemManager() {
    return plugin.getSpecialItemManager();
  }

  private ArenaOptionManager getArenaOptionManager() {
    return plugin.getArenaOptionManager();
  }

}
