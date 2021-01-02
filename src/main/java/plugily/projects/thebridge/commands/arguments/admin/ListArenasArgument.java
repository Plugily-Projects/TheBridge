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

package plugily.projects.thebridge.commands.arguments.admin;

import org.bukkit.command.CommandSender;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.arguments.data.LabelData;
import plugily.projects.thebridge.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class ListArenasArgument {

  public ListArenasArgument(ArgumentsRegistry registry, ChatManager chatManager) {
    registry.mapArgument("thebridgeadmin", new LabeledCommandArgument("list", "thebridge.admin.list", CommandArgument.ExecutorType.BOTH,
      new LabelData("/tba list", "/tba list", "&7Shows list with all loaded arenas\n&6Permission: &7thebridge.admin.list")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(chatManager.colorMessage("Commands.Admin-Commands.List-Command.Header"));
        int i = 0;
        for (Arena arena : ArenaRegistry.getArenas()) {
          sender.sendMessage(chatManager.colorMessage("Commands.Admin-Commands.List-Command.Format").replace("%arena%", arena.getId())
            .replace("%status%", arena.getArenaState().getFormattedName()).replace("%players%", String.valueOf(arena.getPlayers().size()))
            .replace("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
          i++;
        }
        if (i == 0) {
          sender.sendMessage(chatManager.colorMessage("Commands.Admin-Commands.List-Command.No-Arenas"));
        }
      }
    });
  }

}
