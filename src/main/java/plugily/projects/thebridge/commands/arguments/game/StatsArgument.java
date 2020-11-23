package plugily.projects.thebridge.commands.arguments.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.user.User;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class StatsArgument {
  public StatsArgument(ArgumentsRegistry registry, ChatManager chatManager) {
    registry.mapArgument("thebridge", new CommandArgument("stats", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(@NotNull CommandSender sender, String[] args) {
        Player player = args.length == 2 ? Bukkit.getPlayerExact(args[1]) : (Player) sender;
        if (player == null) {
          sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Admin-Commands.Player-Not-Found"));
          return;
        }
        User user = registry.getPlugin().getUserManager().getUser(player);
        if (player.equals(sender)) {
          sender.sendMessage(chatManager.colorMessage("Commands.Stats-Command.Header", player));
        } else {
          sender.sendMessage(chatManager.colorMessage("Commands.Stats-Command.Header-Other", player).replace("%player%", player.getName()));
        }
        sender.sendMessage(chatManager.colorMessage("Commands.Stats-Command.Kills", player) + user.getStat(StatsStorage.StatisticType.KILLS));
        sender.sendMessage(chatManager.colorMessage("Commands.Stats-Command.Deaths", player) + user.getStat(StatsStorage.StatisticType.DEATHS));
        sender.sendMessage(chatManager.colorMessage("Commands.Stats-Command.Wins", player) + user.getStat(StatsStorage.StatisticType.WINS));
        sender.sendMessage(chatManager.colorMessage("Commands.Stats-Command.Loses", player) + user.getStat(StatsStorage.StatisticType.LOSES));
        sender.sendMessage(chatManager.colorMessage("Commands.Stats-Command.Games-Played", player) + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
        sender.sendMessage(chatManager.colorMessage("Commands.Stats-Command.Footer", player));
      }
    });
  }
}
