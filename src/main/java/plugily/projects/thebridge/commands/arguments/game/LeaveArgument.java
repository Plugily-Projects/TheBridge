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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.utils.Debugger;
import plugily.projects.thebridge.utils.Utils;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class LeaveArgument {

  public LeaveArgument(ArgumentsRegistry registry, ChatManager chatManager) {
    registry.mapArgument("thebridge", new CommandArgument("leave", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(!registry.getPlugin().getConfig().getBoolean("Disable-Leave-Command", false)) {
          Player player = (Player) sender;
          if(!Utils.checkIsInGameInstance((Player) sender)) {
            return;
          }
          player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Teleported-To-The-Lobby", player));
          if(registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
            registry.getPlugin().getBungeeManager().connectToHub(player);
            Debugger.debug("{0} was teleported to the Hub server", player.getName());
            return;
          }
          Arena arena = ArenaRegistry.getArena(player);
          ArenaManager.leaveAttempt(player, arena);
          Debugger.debug("{0} has left the arena {1}! Teleported to end location.", player.getName(), arena.getId());
        }
      }
    });
  }

}
