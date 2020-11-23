package plugily.projects.thebridge.commands.arguments.game;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.completion.CompletableArgument;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.user.data.MysqlManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class LeaderboardArgument {

  private final ArgumentsRegistry registry;
  private final ChatManager chatManager;

  public LeaderboardArgument(ArgumentsRegistry registry, ChatManager chatManager) {
    this.registry = registry;
    this.chatManager = chatManager;

    List<String> stats = new ArrayList<>();
    for (StatsStorage.StatisticType value : StatsStorage.StatisticType.values()) {
      if (!value.isPersistent()) {
        continue;
      }
      stats.add(value.name().toLowerCase());
    }
    registry.getTabCompletion().registerCompletion(new CompletableArgument("thebridge", "top", stats));
    registry.mapArgument("thebridge", new CommandArgument("top", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Statistics.Type-Name"));
          return;
        }
        try {
          StatsStorage.StatisticType statisticType = StatsStorage.StatisticType.valueOf(args[1].toUpperCase());
          if (!statisticType.isPersistent()) {
            sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Statistics.Invalid-Name"));
            return;
          }
          printLeaderboard(sender, statisticType);
        } catch (IllegalArgumentException e) {
          sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Statistics.Invalid-Name"));
        }
      }
    });
  }

  private void printLeaderboard(CommandSender sender, StatsStorage.StatisticType statisticType) {
    LinkedHashMap<UUID, Integer> stats = (LinkedHashMap<UUID, Integer>) StatsStorage.getStats(statisticType);
    sender.sendMessage(chatManager.colorMessage("Commands.Statistics.Header"));
    String statistic = StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " "));
    for (int i = 0; i < 10; i++) {
      try {
        UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
        sender.sendMessage(formatMessage(statistic, Bukkit.getOfflinePlayer(current).getName(), i + 1, stats.get(current)));
        stats.remove(current);
      } catch (IndexOutOfBoundsException ex) {
        sender.sendMessage(formatMessage(statistic, "Empty", i + 1, 0));
      } catch (NullPointerException ex) {
        UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
        if (registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
          try (Connection connection = registry.getPlugin().getMysqlDatabase().getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT name FROM " + ((MysqlManager) registry.getPlugin().getUserManager().getDatabase()).getTableName() + " WHERE UUID='" + current.toString() + "'");
            if (set.next()) {
              sender.sendMessage(formatMessage(statistic, set.getString(1), i + 1, stats.get(current)));
              continue;
            }
          } catch (SQLException ignored) {
            //it has failed second time, cannot continue
          }
        }
        sender.sendMessage(formatMessage(statistic, "Unknown Player", i + 1, stats.get(current)));
      }
    }
  }

  private String formatMessage(String statisticName, String playerName, int position, int value) {
    String message = chatManager.colorMessage("Commands.Statistics.Format");
    message = StringUtils.replace(message, "%position%", String.valueOf(position));
    message = StringUtils.replace(message, "%name%", playerName);
    message = StringUtils.replace(message, "%value%", String.valueOf(value));
    message = StringUtils.replace(message, "%statistic%", statisticName);
    return message;
  }

}
