package plugily.projects.thebridge.boot;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.handlers.placeholder.PlaceholderManager;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class PlaceholderInitializer {

  private final Main plugin;

  public PlaceholderInitializer(Main plugin) {
    this.plugin = plugin;
    registerPlaceholders();
  }

  private void registerPlaceholders() {

    getPlaceholderManager().registerPlaceholder(new Placeholder("option_mode", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
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
        if(pluginArena.getMode() == null) {
          return null;
        }
        return pluginArena.getMode().toString();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("base_color", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
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
        if(pluginArena.getBase(player) == null) {
          return null;
        }
        return pluginArena.getBase(player).getColor();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("base_color_formatted", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
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
        if(pluginArena.getBase(player) == null) {
          return null;
        }
        return pluginArena.getBase(player).getFormattedColor();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("summary_winner_base", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
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

    getPlaceholderManager().registerPlaceholder(new Placeholder("summary_winner_base_players", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
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
    getPlaceholderManager().registerPlaceholder(new Placeholder("summary_base_scored", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
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

    getPlaceholderManager().registerPlaceholder(new Placeholder("option_reset_blocks_in", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(PluginArena arena) {
        Arena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return String.valueOf(arena.getArenaOption("RESET_BLOCKS") - pluginArena.getRound());
      }

      @Override
      public String getValue(Player player, PluginArena arena) {
        return getValue(arena);
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

        return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_BASE_" + pluginArena.getMode()).asKey().build();
      }
    });
  }

  private PlaceholderManager getPlaceholderManager() {
    return plugin.getPlaceholderManager();
  }

  private ArenaRegistry getArenaRegistry() {
    return plugin.getArenaRegistry();
  }

}
