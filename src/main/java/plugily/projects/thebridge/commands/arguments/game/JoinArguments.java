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
import plugily.projects.thebridge.arena.ArenaState;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class JoinArguments {

  public JoinArguments(ArgumentsRegistry registry, ChatManager chatManager) {
    //join argument
    registry.mapArgument("thebridge", new CommandArgument("join", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        for (Arena arena : ArenaRegistry.getArenas()) {
          if (args[1].equalsIgnoreCase(arena.getId())) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }
        sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.No-Arena-Like-That"));
      }
    });

    //random join argument, register only for multi arena
    if (!registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      registry.mapArgument("thebridge", new CommandArgument("randomjoin", "", CommandArgument.ExecutorType.PLAYER) {
        @Override
        public void execute(CommandSender sender, String[] args) {
          //first random get method
          Map<Arena, Integer> arenas = new HashMap<>();
          for (Arena arena : ArenaRegistry.getArenas()) {
            if (arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()) {
              arenas.put(arena, arena.getPlayers().size());
            }
          }
          if (arenas.size() > 0) {
            Stream<Map.Entry<Arena, Integer>> sorted = arenas.entrySet().stream().sorted(Map.Entry.comparingByValue());
            Arena arena = sorted.findFirst().get().getKey();
            if (arena != null) {
              ArenaManager.joinAttempt((Player) sender, arena);
              return;
            }
          }

          //fallback safe method
          for (Arena arena : ArenaRegistry.getArenas()) {
            if ((arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)
              && arena.getPlayers().size() < arena.getMaximumPlayers()) {
              ArenaManager.joinAttempt((Player) sender, arena);
              return;
            }
          }
          sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.No-Free-Arenas"));
        }
      });
    }
  }
}
