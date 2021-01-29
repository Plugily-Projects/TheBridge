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

package plugily.projects.thebridge.commands.arguments.admin.level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.arguments.data.LabelData;
import plugily.projects.thebridge.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.thebridge.user.User;
import plugily.projects.thebridge.utils.Utils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 02.01.2021
 */
public class AddLevelArgument {

  public AddLevelArgument(ArgumentsRegistry registry) {
    registry.mapArgument("thebridgeadmin", new LabeledCommandArgument("addlevel", "thebridge.admin.addlevel",
      CommandArgument.ExecutorType.BOTH, new LabelData("/tba addlevel &6<amount> &c[player]", "/tba addlevel <amount>",
      "&7Add level to yourself or target player\n&7Can be used from console too\n&6Permission: &7thebridge.admin.addlevel (for yourself)\n"
        + "&6Permission: &7thebridge.admin.addlevel.others (for others)")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type number of levels to set!");
          return;
        }
        Player target;
        if(args.length == 2) {
          target = (Player) sender;
        } else {
          target = Bukkit.getPlayerExact(args[2]);
        }

        if(target == null) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Admin-Commands.Player-Not-Found"));
          return;
        }

        if(Utils.isInteger(args[1])) {
          User user = registry.getPlugin().getUserManager().getUser(target);
          user.setStat(StatsStorage.StatisticType.LEVEL, user.getStat(StatsStorage.StatisticType.LEVEL) + Integer.parseInt(args[1]));
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.Command-Executed"));
        } else {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.Wrong-Usage")
            .replace("%correct%", "/tba addlevel <amount> [player]"));
        }
      }
    });
  }

}
