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

package plugily.projects.thebridge.commands.arguments.admin.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaState;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.arguments.data.LabelData;
import plugily.projects.thebridge.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.thebridge.utils.Utils;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class StopArgument {

  public StopArgument(ArgumentsRegistry registry) {
    registry.mapArgument("thebridgeadmin", new LabeledCommandArgument("stop", "thebridge.admin.stop", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/tba stop", "/tba stop", "&7Stops the arena you're in\n&7&lYou must be in target arena!\n&6Permission: &7thebridge.admin.stop")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkIsInGameInstance((Player) sender)) {
          return;
        }
        if (ArenaRegistry.getArena((Player) sender).getArenaState() != ArenaState.ENDING) {
          ArenaManager.stopGame(true, ArenaRegistry.getArena((Player) sender));
          sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&e[TheBridge] &aArena successfully stopped!"));
        }
      }
    });
  }

}
