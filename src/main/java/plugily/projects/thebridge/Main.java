package plugily.projects.thebridge;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.permissions.Permission;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.preferences.ConfigOption;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;
import plugily.projects.minigamesbox.classic.utils.services.metrics.Metrics;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaEvents;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaUtils;
import plugily.projects.thebridge.arena.base.BaseMenuHandler;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.admin.CuboidSelector;
import plugily.projects.thebridge.events.PluginEvents;
import plugily.projects.thebridge.handlers.setup.SetupInventory;
import plugily.projects.thebridge.kits.free.BridgeKit;
import plugily.projects.thebridge.kits.free.KnightKit;
import plugily.projects.thebridge.kits.free.LightTankKit;
import plugily.projects.thebridge.kits.level.ArcherKit;
import plugily.projects.thebridge.kits.level.HardcoreKit;
import plugily.projects.thebridge.kits.level.HealerKit;
import plugily.projects.thebridge.kits.level.MediumTankKit;
import plugily.projects.thebridge.kits.level.TerminatorKit;
import plugily.projects.thebridge.kits.premium.HeavyTankKit;
import plugily.projects.thebridge.kits.premium.NakedKit;
import plugily.projects.thebridge.kits.premium.PremiumHardcoreKit;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by Tigerpanzer_02 on 13.03.2022
 */
public class Main extends PluginMain {

  private ArenaRegistry arenaRegistry;
  private ArenaManager arenaManager;
  private ArgumentsRegistry argumentsRegistry;
  private CuboidSelector cuboidSelector;
  private BaseMenuHandler baseMenuHandler;

  @TestOnly
  public Main() {
    super();
  }

  @TestOnly
  protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }

  @Override
  public void onEnable() {
    long start = System.currentTimeMillis();
    registerLocales();
    super.onEnable();
    getDebugger().debug("[System] [Plugin] Initialization start");
    registerPlaceholders();
    addMessages();
    addAdditionalValues();
    initializePluginClasses();
    addKits();
    getDebugger().debug("Full {0} plugin enabled", getName());
    getDebugger().debug("[System] [Plugin] Initialization finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void initializePluginClasses() {
    addFileName("powerups");
    addArenaOptions();
    Arena.init(this);
    ArenaUtils.init(this);
    new ArenaEvents(this);
    arenaManager = new ArenaManager(this);
    arenaRegistry = new ArenaRegistry(this);
    arenaRegistry.registerArenas();
    getSignManager().loadSigns();
    getSignManager().updateSigns();
    argumentsRegistry = new ArgumentsRegistry(this);
    cuboidSelector = new CuboidSelector(this);
    baseMenuHandler = new BaseMenuHandler(this);

    new PluginEvents(this);
    addPluginMetrics();
  }


  public void addKits() {
    long start = System.currentTimeMillis();
    getDebugger().debug("Adding kits...");
    addFileName("kits");
    Class<?>[] classKitNames = new Class[]{BridgeKit.class, KnightKit.class, LightTankKit.class, ArcherKit.class, HealerKit.class,
        MediumTankKit.class, TerminatorKit.class, HardcoreKit.class, PremiumHardcoreKit.class, NakedKit.class, HeavyTankKit.class};
    for(Class<?> kitClass : classKitNames) {
      try {
        kitClass.getDeclaredConstructor().newInstance();
      } catch(Exception e) {
        getLogger().log(Level.SEVERE, "Fatal error while registering existing game kit! Report this error to the developer!");
        getLogger().log(Level.SEVERE, "Cause: " + e.getMessage() + " (kitClass " + kitClass.getName() + ")");
        e.printStackTrace();
      }
    }
    getDebugger().debug("Kit adding finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void registerLocales() {
    Arrays.asList(new Locale("Chinese (Traditional)", "简体中文", "zh_HK", "POEditor contributors", Arrays.asList("中文(傳統)", "中國傳統", "chinese_traditional", "zh")),
            new Locale("Chinese (Simplified)", "简体中文", "zh_CN", "POEditor contributors", Arrays.asList("简体中文", "中文", "chinese", "chinese_simplified", "cn")),
            new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
            new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl")),
            new Locale("English", "English", "en_GB", "Tigerpanzer_02", Arrays.asList("default", "english", "en")),
            new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
            new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
            new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu")),
            new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")),
            new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")),
            new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")),
            new Locale("Lithuanian", "Lietuviešu", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuviešu", "lietuviesu", "lt")),
            new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
            new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br")),
            new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors", Arrays.asList("romanian", "romanesc", "românesc", "ro")),
            new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru")),
            new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
            new Locale("Thai", "Thai", "th_TH", "POEditor contributors", Arrays.asList("thai", "th")),
            new Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")),
            new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors", Arrays.asList("vietnamese", "viet", "việt", "vn")))
        .forEach(LocaleRegistry::registerLocale);
  }
  public void addAdditionalValues() {
    getConfigPreferences().registerOption("FOOD_LOSE", new ConfigOption("Food-Lose", true));

    getStatsStorage().registerStatistic("KILLS", new StatisticType("kills", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("DEATHS", new StatisticType("deaths", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("SCORED_POINTS", new StatisticType("scored_points", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_SCORED_POINTS", new StatisticType("local_scored_points", false, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_KILLS", new StatisticType("local_kills", false, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_POINTS", new StatisticType("local_points", false, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_DEATHS", new StatisticType("local_deaths", false, "int(11) NOT NULL DEFAULT '0'"));

//    getPermissionsManager().registerPermissionCategory("CHANCES_BOOSTER", new PermissionCategory("Chances-Boost", null));
    getPermissionsManager().registerPermission("KIT_PREMIUM_UNLOCK", new Permission("Basic.Premium-Kits", "thebridge.kits.premium"));

    getRewardsHandler().registerRewardType("WIN", new RewardType("win"));
    getRewardsHandler().registerRewardType("LOSE", new RewardType("lose"));
    getRewardsHandler().registerRewardType("PLAYER_DEATH", new RewardType("player-death"));
    getRewardsHandler().registerRewardType("KILL", new RewardType("kill"));
    getRewardsHandler().registerRewardType("DEATH", new RewardType("death"));
    getRewardsHandler().registerRewardType("POINT", new RewardType("point"));
    getRewardsHandler().registerRewardType("RESET_ROUND", new RewardType("reset-round"));

    getSpecialItemManager().registerSpecialItem("BASE_SELECTOR", "Base-Selector");
    getSpecialItemManager().registerSpecialItem("KIT_SELECTOR_MENU", "Kit-Menu");
  }


  public void addMessages() {
    getMessageManager().registerMessage("", new Message("", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_BASE_HEARTS", new Message("In-Game.Messages.Game-End.Placeholders.Base.HEARTS", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_BASE_POINTS", new Message("In-Game.Messages.Game-End.Placeholders.Base.POINTS", ""));
    getMessageManager().registerMessage("SCOREBOARD_BASES_FORMAT", new Message("Scoreboard.Bases.Format", ""));
    getMessageManager().registerMessage("SCOREBOARD_BASES_NOT_INSIDE", new Message("Scoreboard.Bases.Not-Inside", ""));
    getMessageManager().registerMessage("SCOREBOARD_BASES_INSIDE", new Message("Scoreboard.Bases.Inside", ""));

    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_COOLDOWN", new Message("In-Game.Messages.Arena.Cooldown", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_BOW_DAMAGE", new Message("In-Game.Messages.Arena.Bow-Damage", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_DEATH", new Message("In-Game.Messages.Arena.Death", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_DEATH_OWN", new Message("In-Game.Messages.Arena.Death-Own", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_KILLED", new Message("In-Game.Messages.Arena.Killed", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_BUILD_BREAK", new Message("In-Game.Messages.Arena.Build-Break", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_TIME_LEFT", new Message("In-Game.Messages.Arena.Time-Left", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_OWN", new Message("In-Game.Messages.Arena.Portal.Own", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_OPPONENT", new Message("In-Game.Messages.Arena.Portal.Opponent", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_SCORED_TITLE", new Message("In-Game.Messages.Arena.Portal.Scored.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_HOLOGRAM", new Message("In-Game.Messages.Arena.Portal.Hologram", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_OUT", new Message("In-Game.Messages.Arena.Portal.Out", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_BLOCKED_RESET", new Message("In-Game.Messages.Arena.Blocked.Reset", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_BLOCKED_RUN", new Message("In-Game.Messages.Arena.Blocked.Run", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_BLOCKED_TITLE", new Message("In-Game.Messages.Arena.Blocked.Title", ""));

    getMessageManager().registerMessage("BASES_TEAM_FULL", new Message("Bases.Team.Full", ""));
    getMessageManager().registerMessage("BASES_TEAM_EMPTY", new Message("Bases.Team.Empty", ""));
    getMessageManager().registerMessage("BASES_TEAM_INSIDE", new Message("Bases.Team.Inside", ""));
    getMessageManager().registerMessage("BASES_TEAM_NAME", new Message("Bases.Team.Name", ""));
    getMessageManager().registerMessage("BASES_TEAM_MENU", new Message("Bases.Team.Menu", ""));
    getMessageManager().registerMessage("BASES_TEAM_CHOOSE", new Message("Bases.Team.Choose", ""));
    getMessageManager().registerMessage("BASES_TEAM_MEMBER", new Message("Bases.Team.Member", ""));
    getMessageManager().registerMessage("BASES_COLORS", new Message("Bases.Colors", ""));
    //BRIDGE

    getMessageManager().registerMessage("KIT_CONTENT_BRIDGE_NAME", new Message("Kit.Content.Bridge.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_BRIDGE_DESCRIPTION", new Message("Kit.Content.Bridge.Description", ""));

    //KNIGHT

    getMessageManager().registerMessage("KIT_CONTENT_KNIGHT_NAME", new Message("Kit.Content.Knight.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_KNIGHT_DESCRIPTION", new Message("Kit.Content.Knight.Description", ""));

//LIGHT_TANK

    getMessageManager().registerMessage("KIT_CONTENT_LIGHT_TANK_NAME", new Message("Kit.Content.Light-Tank.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_LIGHT_TANK_DESCRIPTION", new Message("Kit.Content.Light-Tank.Description", ""));
    //ARCHER

    getMessageManager().registerMessage("KIT_CONTENT_ARCHER_NAME", new Message("Kit.Content.Archer.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_ARCHER_DESCRIPTION", new Message("Kit.Content.Archer.Description", ""));
//HARDCORE

    getMessageManager().registerMessage("KIT_CONTENT_HARDCORE_NAME", new Message("Kit.Content.Hardcore.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_HARDCORE_DESCRIPTION", new Message("Kit.Content.Hardcore.Description", ""));
//HEALER

    getMessageManager().registerMessage("KIT_CONTENT_HEALER_NAME", new Message("Kit.Content.Healer.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_HEALER_DESCRIPTION", new Message("Kit.Content.Healer.Description", ""));
//MEDIUM_TANK

    getMessageManager().registerMessage("KIT_CONTENT_MEDIUM_TANK_NAME", new Message("Kit.Content.Medium-Tank.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_MEDIUM_TANK_DESCRIPTION", new Message("Kit.Content.Medium-Tank.Description", ""));
//TERMINATOR

    getMessageManager().registerMessage("KIT_CONTENT_TERMINATOR_NAME", new Message("Kit.Content.Terminator.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_TERMINATOR_DESCRIPTION", new Message("Kit.Content.Terminator.Description", ""));
//HEAVY_TANK

    getMessageManager().registerMessage("KIT_CONTENT_HEAVY_TANK_NAME", new Message("Kit.Content.Heavy-Tank.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_HEAVY_TANK_DESCRIPTION", new Message("Kit.Content.Heavy-Tank.Description", ""));
//WILD_NAKED

    getMessageManager().registerMessage("KIT_CONTENT_WILD_NAKED_NAME", new Message("Kit.Content.Wild-Naked.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WILD_NAKED_DESCRIPTION", new Message("Kit.Content.Wild-Naked.Description", ""));
    getMessageManager().registerMessage("KIT_CONTENT_WILD_NAKED_CANNOT_WEAR_ARMOR", new Message("Kit.Content.Wild-Naked.Cannot-Wear-Armor", ""));
//PREMIUM_HARDCORE

    getMessageManager().registerMessage("KIT_CONTENT_PREMIUM_HARDCORE_NAME", new Message("Kit.Content.Premium-Hardcore.Name", ""));
    getMessageManager().registerMessage("KIT_CONTENT_PREMIUM_HARDCORE_DESCRIPTION", new Message("Kit.Content.Premium-Hardcore.Description", ""));

    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_KILLS", new Message("Leaderboard.Statistics.Kills", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_DEATHS", new Message("Leaderboard.Statistics.Deaths", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_SCORED_POINTS", new Message("Leaderboard.Statistics.Score", ""));

  }



  public void registerPlaceholders() {

    getPlaceholderManager().registerPlaceholder(new Placeholder("option_mode", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL){
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getMode(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getMode(arena);
      }

      @Nullable
      private String getMode(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getMode().toString();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("base_color", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL){
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getBaseValue(arena, player);
      }

      @Override
      public String getValue(PluginArena arena) {
        return "";
      }

      @Nullable
      private String getBaseValue(PluginArena arena, Player player) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getBase(player).getColor();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("base_color_formatted", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL){
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getBaseValue(arena, player);
      }

      @Override
      public String getValue(PluginArena arena) {
        return "";
      }

      @Nullable
      private String getBaseValue(PluginArena arena, Player player) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getBase(player).getFormattedColor();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("arena_summary_winner_base", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena.getWinner() == null) {
          return "";
        }
        return pluginArena.getWinner().getFormattedColor();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("arena_summary_winner_base_players", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena.getWinner() == null) {
          return "";
        }
        StringBuilder baseMember = new StringBuilder();
        for(Player p : pluginArena.getWinner().getAlivePlayers()) {
          if(pluginArena.getWinner().getAlivePlayers().size() > 1) {
            baseMember.append(p.getName()).append(" (").append(arena.getPlugin().getUserManager().getUser(p).getStatistic("LOCAL_SCORED_POINTS")).append("), ");
          } else {
            baseMember.append(p.getName());
          }
        }
        if(pluginArena.getWinner().getAlivePlayers().size() > 1) {
          baseMember.deleteCharAt(baseMember.length() - 2);
        }

        return baseMember.toString();

      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("arena_summary_base_scored", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena.getWinner() == null) {
          return "";
        }
        int baseScored = 0;
        for(Player p : pluginArena.getWinner().getAlivePlayers()) {
          baseScored += arena.getPlugin().getUserManager().getUser(p).getStatistic("LOCAL_SCORED_POINTS");
        }
        return String.valueOf(baseScored);
      }
    });


    getPlaceholderManager().registerPlaceholder(new Placeholder("summary_player", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena, player);
      }

      @Override
      public String getValue(PluginArena arena) {
        return "";
      }

      @Nullable
      private String getSummary(PluginArena arena, Player player) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena.getWinner() == null) {
          return "";
        }
        String summaryEnding;
        if(pluginArena.getWinner().getPlayers().contains(player)) {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WIN").asKey().arena(pluginArena).build();
        } else {
          summaryEnding = new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_LOSE").asKey().arena(pluginArena).build();
        }
        return summaryEnding;
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("summary", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena.getWinner() == null) {
          return "";
        }
        return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_BASE_"+pluginArena.getMode().toString()).asKey().arena(pluginArena).build();
      }
    });
  }


  private void addPluginMetrics() {
    getMetrics().addCustomChart(new Metrics.SimplePie("hooked_addons", () -> {
      if(getServer().getPluginManager().getPlugin("TheBridge-Enlargement") != null) {
        return "Enlargement";
      }
      return "None";
    }));
  }

  private void addArenaOptions() {
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

  @Override
  public ArenaRegistry getArenaRegistry() {
    return arenaRegistry;
  }

  @Override
  public ArgumentsRegistry getArgumentsRegistry() {
    return argumentsRegistry;
  }

  @Override
  public ArenaManager getArenaManager() {
    return arenaManager;
  }


  public BaseMenuHandler getBaseMenuHandler() {
    return baseMenuHandler;
  }

  public CuboidSelector getCuboidSelector() {
    return cuboidSelector;
  }


  @Override
  public PluginSetupInventory openSetupInventory(PluginArena arena, Player player) {
    return new SetupInventory(this, arena, player);
  }

  @Override
  public PluginSetupInventory openSetupInventory(PluginArena arena, Player player, SetupUtilities.InventoryStage inventoryStage) {
    return new SetupInventory(this, arena, player, inventoryStage);
  }
}
