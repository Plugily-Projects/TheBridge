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
 * @author Tigerpanzer_02, 2Wild4You
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
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Level") + user.getStat(StatsStorage.StatisticType.LEVEL));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Exp") + user.getStat(StatsStorage.StatisticType.XP));
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Next-Level-Exp")
          + Math.ceil(Math.pow(50.0 * user.getStat(StatsStorage.StatisticType.LEVEL), 1.5)));
        sender.sendMessage(chatManager.colorMessage("Commands.Stats-Command.Footer", player));
      }
    });
  }
}
